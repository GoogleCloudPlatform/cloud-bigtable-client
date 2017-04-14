/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.grpc.io;

import com.google.api.client.util.BackOff;
import com.google.api.client.util.Clock;
import com.google.api.client.util.Sleeper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.bigtable.config.Logger;
import com.google.cloud.bigtable.config.RetryOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.GuardedBy;

/**
 * Client interceptor that authenticates all calls by binding header data provided by a credential.
 * Typically this will populate the Authorization header but other headers may also be filled out.
 * <p>
 * Uses the new and simplified Google auth library:
 * https://github.com/google/google-auth-library-java
 * </p>
 * <p>
 * TODO: COPIED FROM io.grpc.auth.ClientAuthInterceptor. The logic added here for initialization and
 * locking could be moved back to gRPC. This implementation takes advantage of the fact that all of
 * the Bigtable endpoints are OAuth2 based. It uses the OAuth AccessToken to get the token value and
 * next refresh time. The refresh is scheduled asynchronously.
 * </p>
 *
 * @author sduskis
 * @version $Id: $Id
 */
public class RefreshingOAuth2CredentialsInterceptor implements ClientInterceptor {

  /**
   * <p>
   * This enum describes the states of the OAuth header.
   * </p>
   * <ol>
   * <li>Good - fine to use, and does not need to be refreshed.
   * <li>Stale - fine to use, but requires an async refresh
   * <li>Expired - Cannot be used. Wait for a new token to be loaded
   * </ol>
   */
  @VisibleForTesting
  enum CacheState {
    Good, Stale, Expired, Exception
  }

  enum RetryState {
    PerformRetry, RetriesExhausted, Interrupted
  }

  private static final Logger LOG = new Logger(RefreshingOAuth2CredentialsInterceptor.class);
  private static final Metadata.Key<String> AUTHORIZATION_HEADER_KEY = Metadata.Key.of(
      "Authorization", Metadata.ASCII_STRING_MARSHALLER);

  @VisibleForTesting
  static Clock clock = Clock.SYSTEM;

  // From MoreExecutors
  private static final boolean isAppEngine = System.getProperty("com.google.appengine.runtime.environment") != null;

  @VisibleForTesting
  static class HeaderCacheElement {

    /**
     * This specifies how far in advance of a header expiration do we consider the token stale. The
     * Stale state indicates that the interceptor needs to do an asynchronous refresh.
     */
    static final int TOKEN_STALENESS_MS = 75 * 1000;

    /**
     * After the token is "expired," the interceptor blocks gRPC calls. The Expired state indicates
     * that the interceptor needs to do a synchronous refresh.
     */
    static final int TOKEN_EXPIRES_MS = 15 * 1000;

    final Status status;
    final String header;
    final long actualExpirationTimeMs;

    HeaderCacheElement(AccessToken token) {
      this.status = Status.OK;
      if (token.getExpirationTime() == null) {
        actualExpirationTimeMs = Long.MAX_VALUE;
      } else {
        actualExpirationTimeMs = token.getExpirationTime().getTime();
      }
      header = "Bearer " + token.getTokenValue();
    }

    HeaderCacheElement(String header, long actualExpirationTimeMs) {
      this.status = Status.OK;
      this.header = header;
      this.actualExpirationTimeMs = actualExpirationTimeMs;
    }

    HeaderCacheElement(Status errorStatus) {
      Preconditions.checkArgument(!errorStatus.isOk(), "Error status can't be OK");
      this.status = errorStatus;
      this.header = null;
      this.actualExpirationTimeMs = 0;
    }

    CacheState getCacheState() {
      long now = clock.currentTimeMillis();

      if (!status.isOk()) {
        return CacheState.Exception;
      } else if (actualExpirationTimeMs - TOKEN_EXPIRES_MS <= now) {
        return CacheState.Expired;
      } else if (actualExpirationTimeMs - TOKEN_STALENESS_MS <= now) {
        return CacheState.Stale;
      } else {
        return CacheState.Good;
      }
    }
  }

  private static final HeaderCacheElement EMPTY_HEADER = new HeaderCacheElement(null, 0);

  private final Logger logger;
  private final ExecutorService executor;
  @VisibleForTesting
  Sleeper sleeper = Sleeper.DEFAULT;

  private final RetryOptions retryOptions;

  private final OAuth2Credentials credentials;

  @VisibleForTesting
  final Object lock = new Object();

  @VisibleForTesting
  @GuardedBy("lock")
  HeaderCacheElement headerCache = EMPTY_HEADER;

  @VisibleForTesting
  @GuardedBy("lock")
  boolean isRefreshing = false;

  @GuardedBy("lock")
  private Future<HeaderCacheElement> futureToken = null;


  /**
   * <p>Constructor for RefreshingOAuth2CredentialsInterceptor.</p>
   *
   * @param scheduler a {@link ExecutorService} object.
   * @param credentials a {@link OAuth2Credentials} object.
   * @param retryOptions a {@link RetryOptions} object.
   */
  public RefreshingOAuth2CredentialsInterceptor(ExecutorService scheduler,
      OAuth2Credentials credentials, RetryOptions retryOptions) {
    this(scheduler, credentials, retryOptions, LOG);
  }

  @VisibleForTesting
  RefreshingOAuth2CredentialsInterceptor(ExecutorService scheduler, OAuth2Credentials credentials,
      RetryOptions retryOptions, Logger logger) {
    this.executor = Preconditions.checkNotNull(scheduler);
    this.credentials = Preconditions.checkNotNull(credentials);
    this.retryOptions = Preconditions.checkNotNull(retryOptions);
    this.logger = Preconditions.checkNotNull(logger);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
      CallOptions callOptions, Channel next) {
    return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        HeaderCacheElement headerCache = getHeaderSafe();

        if (!headerCache.status.isOk()) {
          responseListener.onClose(headerCache.status, new Metadata());
          return;
        }

        headers.put(AUTHORIZATION_HEADER_KEY, headerCache.header);
        delegate().start(responseListener, headers);
      }
    };
  }

  private HeaderCacheElement getHeaderSafe() {
    try {
      return getHeader();
    } catch (Exception e) {
      return new HeaderCacheElement(
          Status.UNAUTHENTICATED
              .withDescription("Unexpected failure get auth token")
              .withCause(e)
      );
    }
  }

  /**
   * Get the http credential header we need from a new oauth2 AccessToken.
   */
  private HeaderCacheElement getHeader() throws ExecutionException, InterruptedException, TimeoutException {
    final Future<HeaderCacheElement> deferredResult;

    synchronized (lock) {
      CacheState state = headerCache.getCacheState();

      // AppEngine doesn't allow threads to outlive the request, so disable background refresh
      if (isAppEngine && state == CacheState.Stale) {
        state = CacheState.Good;
      }

      if (state == CacheState.Good || state == CacheState.Exception) {
        return headerCache;
      } else if (state == CacheState.Stale) {
        asyncRefresh();
        return headerCache;
      } else if (state == CacheState.Expired) {
        // defer the future resolution (asyncRefresh will spin up a thread that will try to acquire the lock)
        deferredResult = asyncRefresh();
      } else {
        return new HeaderCacheElement(
            Status.UNAUTHENTICATED
                .withCause(new IllegalStateException("Could not process state: " + state))
        );
      }
    }
    return deferredResult.get(250, TimeUnit.MILLISECONDS);
  }

  /**
   * Refresh the credentials and block. Will return an error if the credentials haven't
   * been refreshed.
   * This method should not be called while holding the refresh lock
   */
  HeaderCacheElement syncRefresh() {
    try {
      return asyncRefresh().get(250, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return new HeaderCacheElement(
          Status.UNAUTHENTICATED
              .withCause(e)
      );
    }
  }

  /**
   * Refreshes the OAuth2 token asynchronously. This method will only start an async refresh if
   * there isn't a currently running asynchronous refresh.
   */
  Future<HeaderCacheElement> asyncRefresh() {
    synchronized (lock) {
      if (isRefreshing) {
        return futureToken;
      }
      isRefreshing = true;

      try {
        this.futureToken = executor.submit(new Callable<HeaderCacheElement>() {
          @Override
          public HeaderCacheElement call() throws Exception {
            HeaderCacheElement newToken = refreshCredentialsWithRetry();
            return updateToken(newToken);
          }
        });
      } catch (RuntimeException e) {
        isRefreshing = false;
        throw e;
      }

      return this.futureToken;
    }
  }

  private HeaderCacheElement updateToken(HeaderCacheElement newToken) {
    synchronized (lock) {
      // Update the token only if the new token is good or the old token is bad
      CacheState newState = newToken.getCacheState();
      boolean newTokenOk = newState == CacheState.Good || newState == CacheState.Stale;
      CacheState oldCacheState = headerCache.getCacheState();
      boolean oldTokenOk = oldCacheState == CacheState.Good || oldCacheState == CacheState.Stale;

      if (newTokenOk || !oldTokenOk) {
        headerCache = newToken;
      } else {
        LOG.warn("Failed to refresh the access token. Falling back to existing token. "
            + "New token state: {}, status: {}", newState, newToken.status);
      }
      futureToken = null;
      isRefreshing = false;

      return headerCache;
    }
  }

  /**
   * Calls {@link com.google.auth.oauth2.OAuth2Credentials#refreshAccessToken()}. In case of an
   * IOException, retry the call as per the {@link com.google.api.client.util.BackOff} policy
   * defined by {@link com.google.cloud.bigtable.config.RetryOptions#createBackoff()}.
   *
   * <p>This method retries until one of the following conditions occurs:
   *
   * <ol>
   * <li>An OAuth request was completed. If the value is null, return an exception.
   * <li>A non-IOException Exception is thrown - return an error status
   * <li>All retries have been exhausted, i.e. when the Backoff.nextBackOffMillis() returns
   * BackOff.STOP
   * <li>An interrupt occurs.
   * </ol>
   *
   * @return HeaderCacheElement containing either a valid {@link com.google.auth.oauth2.AccessToken} or an exception.
   */
  @VisibleForTesting
  HeaderCacheElement refreshCredentialsWithRetry() {
    final BackOff backoff = retryOptions.createBackoff();

    while (true) {
      try {
        logger.info("Refreshing the OAuth token");
        AccessToken newToken = credentials.refreshAccessToken();
        return new HeaderCacheElement(newToken);
      } catch (IOException exception) {
        logger.warn("Got an unexpected IOException when refreshing google credentials.", exception);
        // An IOException occurred. Retry with backoff.
        // Given the backoff, either sleep for a short duration, or terminate if the backoff has
        // reached its configured timeout limit.
        RetryState retryState = getRetryState(backoff);
        if (retryState != RetryState.PerformRetry) {
          return new HeaderCacheElement(
              Status.UNAUTHENTICATED
                  .withDescription("Exhausted retries trying to authenticate")
                  .withCause(exception)
          );
        } // else Retry.

      } catch (Exception e) {
        logger.warn("Got an unexpected exception while trying to refresh google credentials.", e);
        return new HeaderCacheElement(
            Status.UNAUTHENTICATED
                .withDescription("Unexpected error trying to authenticate")
                .withCause(e)
        );
      }
    }
  }

  /**
   * Sleep and/or determine if the backoff has timed out.
   *
   * @param backoff a {@link com.google.api.client.util.BackOff} object.
   * @return RetryState indicating the current state of the retry logic.
   */
  RetryState getRetryState(BackOff backoff) {
    final long nextBackOffMillis;
    try {
      nextBackOffMillis = backoff.nextBackOffMillis();
    } catch (IOException e) {
      // Should never happen since we use an ExponentialBackoff
      throw Throwables.propagate(e);
    }
    if (nextBackOffMillis == BackOff.STOP) {
      logger.warn("Exhausted the number of retries for credentials refresh after "
          + this.retryOptions.getMaxElaspedBackoffMillis() + " milliseconds.");
      return RetryState.RetriesExhausted;
    }
    try {
      sleeper.sleep(nextBackOffMillis);
      // Try to perform another call.
      return RetryState.PerformRetry;
    } catch (InterruptedException e) {
      logger.warn("Interrupted while trying to refresh credentials.");
      Thread.currentThread().interrupt();
      // If the thread is interrupted, terminate immediately.
      return RetryState.Interrupted;
    }
  }
}
