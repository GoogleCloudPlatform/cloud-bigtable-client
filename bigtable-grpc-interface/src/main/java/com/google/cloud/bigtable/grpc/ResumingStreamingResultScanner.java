/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.cloud.bigtable.grpc;

import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.ExponentialBackOff.Builder;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Sleeper;
import com.google.bigtable.v1.ReadRowsRequest;
import com.google.bigtable.v1.Row;
import com.google.protobuf.ByteString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.grpc.Status;

import java.io.IOException;


/**
 * A ResultScanner that attempts to resume the readRows call when it
 * encounters gRPC INTERNAL errors.
 */
public class ResumingStreamingResultScanner extends AbstractBigtableResultScanner {

  protected static final Log LOG = LogFactory.getLog(ResumingStreamingResultScanner.class);

  private static final ByteString NEXT_ROW_SUFFIX = ByteString.copyFrom(new byte[]{0x00});
  private final BigtableResultScannerFactory scannerFactory;

  /**
   * Construct a ByteString containing the next possible row key.
   */
  static ByteString nextRowKey(ByteString previous) {
    return previous.concat(NEXT_ROW_SUFFIX);
  }

  private final Builder backOffBuilder;
  private final ReadRowsRequest originalRequest;
  private final boolean retryOnDeadlineExceeded;

  private BackOff currentBackoff;
  private ResultScanner<Row> currentDelegate;
  private ByteString lastRowKey = null;
  private Sleeper sleeper = Sleeper.DEFAULT;

  public ResumingStreamingResultScanner(
      RetryOptions retryOptions,
      ReadRowsRequest originalRequest,
      BigtableResultScannerFactory scannerFactory) {
    Preconditions.checkArgument(
        !originalRequest.getAllowRowInterleaving(),
        "Row interleaving is not supported when using resumable streams");
    // TODO(kevin): remove this flag
    retryOnDeadlineExceeded = retryOptions.retryOnDeadlineExceeded();
    this.backOffBuilder = new ExponentialBackOff.Builder()
        .setInitialIntervalMillis(retryOptions.getInitialBackoffMillis())
        .setMaxElapsedTimeMillis(retryOptions.getMaxElaspedBackoffMillis())
        .setMultiplier(retryOptions.getBackoffMultiplier());
    this.originalRequest = originalRequest;
    this.scannerFactory = scannerFactory;
    this.currentBackoff = backOffBuilder.build();
    this.currentDelegate = scannerFactory.createScanner(originalRequest);
  }

  @Override
  public Row next() throws IOException {
    while (true) {
      try {
        Row result = currentDelegate.next();
        if (result != null) {
          lastRowKey = result.getKey();
        }
        // We've had at least one successful RPC, reset the backoff
        currentBackoff.reset();

        return result;
      } catch (ReadTimeoutException rte) {
        LOG.warn("ReadTimeoutException: ", rte);
        backOffAndRetry(rte);
      } catch (IOExceptionWithStatus ioe) {
        LOG.warn("IOExceptionWithStatus: ", ioe);
        Status.Code code = ioe.getStatus().getCode();
        if (RetryOptions.RETRIABLE_ERROR_CODES.contains(code)) {
          backOffAndRetry(ioe);
        } else {
          throw ioe;
        }
      }
    }
  }

  /**
   * Backs off and reissues request.
   *
   * @param cause
   * @throws IOException
   * @throws BigtableRetriesExhaustedException if retry is exhausted.
   */
  private void backOffAndRetry(IOException cause) throws IOException,
      BigtableRetriesExhaustedException {
    long nextBackOff = currentBackoff.nextBackOffMillis();
    if (nextBackOff == BackOff.STOP) {
      LOG.warn("RetriesExhausted: ", cause);
      throw new BigtableRetriesExhaustedException(
          "Exhausted streaming retries.", cause);
    }

    sleep(nextBackOff);
    reissueRequest();
  }

  @Override
  public void close() throws IOException {
    currentDelegate.close();
  }

  private void reissueRequest() {
    try {
      currentDelegate.close();
    } catch (IOException ioe) {
      LOG.warn("Error closing scanner before reissuing request: ", ioe);
    }

    ReadRowsRequest.Builder newRequest = originalRequest.toBuilder();
    if (lastRowKey != null) {
      newRequest.getRowRangeBuilder().setStartKey(nextRowKey(lastRowKey));
    }
    currentDelegate = scannerFactory.createScanner(newRequest.build());
  }

  private void sleep(long millis) throws IOException {
    try {
      sleeper.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while sleeping for resume", e);
    }
  }
}
