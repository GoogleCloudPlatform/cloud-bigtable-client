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
// Because MasterKeepAliveConnection is default scope, we have to use this package.  :-/
package org.apache.hadoop.hbase.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Threads;

import com.google.cloud.bigtable.grpc.BigtableClient;
import com.google.cloud.bigtable.grpc.BigtableGrpcClient;
import com.google.cloud.bigtable.grpc.BigtableTableAdminClient;
import com.google.cloud.bigtable.grpc.BigtableTableAdminGrpcClient;
import com.google.cloud.bigtable.grpc.ChannelOptions;
import com.google.cloud.bigtable.grpc.TransportOptions;
import com.google.cloud.bigtable.hbase.BigtableBufferedMutator;
import com.google.cloud.bigtable.hbase.BigtableOptions;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import com.google.cloud.bigtable.hbase.BigtableRegionLocator;
import com.google.cloud.bigtable.hbase.BigtableTable;
import com.google.cloud.bigtable.hbase.Logger;
import com.google.common.base.MoreObjects;

public class BigtableConnection implements Connection, Closeable {
  public static final String MAX_INFLIGHT_RPCS_KEY =
      "google.bigtable.buffered.mutator.max.inflight.rpcs";

  // Default rpc count per channel.
  public static final int MAX_INFLIGHT_RPCS_DEFAULT = 50;

  /**
   * The maximum amount of memory to be used for asynchronous buffered mutator RPCs.
   */
  public static final String BIGTABLE_BUFFERED_MUTATOR_MAX_MEMORY_KEY =
      "google.bigtable.buffered.mutator.max.memory";

  private static final AtomicLong SEQUENCE_GENERATOR = new AtomicLong();
  private static final Map<Long, BigtableBufferedMutator> ACTIVE_BUFFERED_MUTATORS =
      Collections.synchronizedMap(new HashMap<Long, BigtableBufferedMutator>());

  static {
    Runnable shutDownRunnable = new Runnable() {
      @Override
      public void run() {
        for (BigtableBufferedMutator bbm : ACTIVE_BUFFERED_MUTATORS.values()) {
          if (bbm.hasInflightRequests()) {
            int size = ACTIVE_BUFFERED_MUTATORS.size();
            LOG.warn("Shutdown is commencing and you have open %d buffered mutators."
                + "You need to close() or flush() them so that is not lost", size);
            break;
          }
        }
      }
    };
    Runtime.getRuntime().addShutdownHook(new Thread(shutDownRunnable));
  }

  // Default to 32MB.
  //
  // HBase uses 2MB as the write buffer size.  Their limit is the size to reach before performing
  // a batch async write RPC.  Our limit is a memory cap for async RPC after which we throttle.
  // HBase does not throttle like we do.
  public static final long BIGTABLE_BUFFERED_MUTATOR_MAX_MEMORY_DEFAULT =
      16 * TableConfiguration.WRITE_BUFFER_SIZE_DEFAULT;

  private static final Logger LOG = new Logger(BigtableConnection.class);

  private static final Set<RegionLocator> locatorCache = new CopyOnWriteArraySet<>();

  private final Configuration conf;
  private volatile boolean closed;
  private volatile boolean aborted;
  private volatile ExecutorService batchPool = null;
  private BigtableClient client;
  private BigtableTableAdminClient bigtableTableAdminClient;

  private volatile boolean cleanupPool = false;
  private final BigtableOptions options;
  private final TableConfiguration tableConfig;

  // A set of tables that have been disabled via BigtableAdmin.
  private Set<TableName> disabledTables = new HashSet<>();

  public BigtableConnection(Configuration conf) throws IOException {
    this(conf, false, null, null);
  }

  BigtableConnection(Configuration conf, boolean managed, ExecutorService pool, User user)
      throws IOException {
    this.batchPool = pool;
    this.closed = false;
    this.conf = conf;
    if (managed) {
      throw new IllegalArgumentException("Bigtable does not support managed connections.");
    }

    if (batchPool == null) {
      batchPool = getBatchPool();
    }

    this.options = BigtableOptionsFactory.fromConfiguration(conf);
    TransportOptions dataTransportOptions = options.getDataTransportOptions();
    ChannelOptions channelOptions = options.getChannelOptions();
    TransportOptions tableAdminTransportOptions = options.getTableAdminTransportOptions();

    LOG.info("Opening connection for project %s on data host %s, "
        + "table admin host %s, using transport %s.",
        options.getProjectId(),
        dataTransportOptions.getHost(),
        tableAdminTransportOptions.getHost(),
        dataTransportOptions.getTransport());

    this.client = getBigtableClient(
        dataTransportOptions,
        channelOptions,
        batchPool);
    this.bigtableTableAdminClient = getTableAdminClient(
        tableAdminTransportOptions,
        channelOptions,
        batchPool);
    this.tableConfig = new TableConfiguration(conf);
  }

  private BigtableTableAdminClient getTableAdminClient(
      TransportOptions tableAdminTransportOptions,
      ChannelOptions channelOptions,
      ExecutorService executorService) {

    return BigtableTableAdminGrpcClient.createClient(
        tableAdminTransportOptions, channelOptions, executorService);
  }

  protected BigtableClient getBigtableClient(
      TransportOptions dataTransportOptions,
      ChannelOptions channelOptions,
      ExecutorService executorService) {

    return BigtableGrpcClient.createClient(
        dataTransportOptions, channelOptions, executorService);
  }

  @Override
  public Configuration getConfiguration() {
    return this.conf;
  }

  @Override
  public Table getTable(TableName tableName) throws IOException {
    return getTable(tableName, getBatchPool());
  }

  @Override
  public Table getTable(TableName tableName, ExecutorService pool) throws IOException {
    return new BigtableTable(this, tableName, options, client, pool);
  }

  @Override
  public BufferedMutator getBufferedMutator(BufferedMutatorParams params) throws IOException {
    if (params.getTableName() == null) {
      throw new IllegalArgumentException("TableName cannot be null.");
    }
    if (params.getPool() == null) {
      params.pool(getBatchPool());
    }
    if (params.getWriteBufferSize() == BufferedMutatorParams.UNSET) {
      params.writeBufferSize(tableConfig.getWriteBufferSize());
    }

    int defaultRpcCount = MAX_INFLIGHT_RPCS_DEFAULT * options.getChannelCount();
    int maxInflightRpcs = conf.getInt(MAX_INFLIGHT_RPCS_KEY, defaultRpcCount);

    final long id = SEQUENCE_GENERATOR.incrementAndGet();

    BigtableBufferedMutator bigtableBufferedMutator = new BigtableBufferedMutator(conf,
        params.getTableName(),
        maxInflightRpcs,
        params.getWriteBufferSize(),
        client,
        options,
        params.getPool(),
        params.getListener()){

      @Override
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          ACTIVE_BUFFERED_MUTATORS.remove(id);
        }
      }
    };
    ACTIVE_BUFFERED_MUTATORS.put(id, bigtableBufferedMutator);
    return bigtableBufferedMutator;
  }

  @Override
  public BufferedMutator getBufferedMutator(TableName tableName) throws IOException {
    long maxMemory = conf.getLong(
        BIGTABLE_BUFFERED_MUTATOR_MAX_MEMORY_KEY,
        BIGTABLE_BUFFERED_MUTATOR_MAX_MEMORY_DEFAULT);
    return getBufferedMutator(
        new BufferedMutatorParams(tableName)
            .writeBufferSize(maxMemory));
  }

  /** This should not be used.  The hbase shell needs this in hbsae 0.99.2.  Remove this once
   * 1.0.0 comes out
   */
  @Deprecated
  public Table getTable(String tableName) throws IOException {
    return getTable(TableName.valueOf(tableName));
  }

  @Override
  public RegionLocator getRegionLocator(TableName tableName) throws IOException {
    for (RegionLocator locator : locatorCache) {
      if (locator.getName().equals(tableName)) {
        return locator;
      }
    }

    RegionLocator newLocator = new BigtableRegionLocator(tableName, options, client);

    if (locatorCache.add(newLocator)) {
      return newLocator;
    }

    for (RegionLocator locator : locatorCache) {
      if (locator.getName().equals(tableName)) {
        return locator;
      }
    }

    throw new IllegalStateException(newLocator + " was supposed to be in the cache");
  }

  @Override
  public Admin getAdmin() throws IOException {
    return new BigtableAdmin(options, conf, this, bigtableTableAdminClient, this.disabledTables);
  }

  @Override
  public void abort(final String msg, Throwable t) {
    if (t != null) {
      LOG.fatal(msg, t);
    } else {
      LOG.fatal(msg);
    }
    this.aborted = true;
    close();
    this.closed = true;
  }

  @Override
  public boolean isClosed() {
    return this.closed;
  }

  @Override
  public boolean isAborted() {
    return this.aborted;
  }

  private void shutdownClients() {
    Exception toBeThrown = null;
    try {
      bigtableTableAdminClient.close();
    } catch (Exception e) {
      LOG.error("Exception when shutting down the table admin client.", e);
      toBeThrown = e;
    }
    try {
      client.close();
    } catch (Exception e) {
      LOG.error("Exception when shutting down the data client.", e);
      // We will lose the table admin exception if there was one, but
      // attempting to carry both exceptions will lead to the stack traces
      // not printed in most cases (see RetriesExhausted for that happening):
      toBeThrown = e;
    }
    if (toBeThrown != null) {
      throw new RuntimeException("Error when shutting down clients", toBeThrown);
    }
  }

  @Override
  public void close() {
    if (this.closed) {
      return;
    }
    try {
      shutdownClients();
    } finally {
      this.closed = true;
    }
    // If the clients are shutdown, there shouldn't be any more activity on the
    // batch pool (assuming we created it ourselves). If exceptions were raised
    // shutting down the clients, it's not entirely safe to shutdown the pool
    // (via a finally block).
    shutdownBatchPool();
  }

  @Override
  public String toString() {
    InetAddress dataHost = options.getDataHost();
    InetAddress tableAdminHost = options.getTableAdminHost();
    return MoreObjects.toStringHelper(BigtableConnection.class)
      .add("zone", options.getZone())
      .add("project", options.getProjectId())
      .add("cluster", options.getCluster())
      .add("dataHost", dataHost)
      .add("tableAdminHost", tableAdminHost)
      .toString();
  }

  // Copied from org.apache.hadoop.hbase.client.HConnectionManager#getBatchPool()
  private ExecutorService getBatchPool() {
    if (batchPool == null) {
      // shared HTable thread executor not yet initialized
      synchronized (this) {
        if (batchPool == null) {
          int maxThreads = getMaxThreads();
          long keepAliveTime = conf.getLong(
              "hbase.hconnection.threads.keepalivetime", 60);
          LinkedBlockingQueue<Runnable> workQueue =
              new LinkedBlockingQueue<Runnable>(128 *
                  conf.getInt("hbase.client.max.total.tasks", 200));
          this.batchPool = new ThreadPoolExecutor(
              maxThreads,
              maxThreads,
              keepAliveTime,
              TimeUnit.SECONDS,
              workQueue,
              Threads.newDaemonThreadFactory("bigtable-connection-shared-executor"));
        }
        this.cleanupPool = true;
      }
    }
    return this.batchPool;
  }

  private int getMaxThreads() {
    int maxThreads = conf.getInt("hbase.hconnection.threads.max", 256);
    if (maxThreads == 0) {
      maxThreads = Runtime.getRuntime().availableProcessors() * 8;
    }
    return maxThreads;
  }

  // Copied from org.apache.hadoop.hbase.client.HConnectionManager#shutdownBatchPool()
  private void shutdownBatchPool() {
    if (this.cleanupPool && this.batchPool != null && !this.batchPool.isShutdown()) {
      this.batchPool.shutdown();
      try {
        if (!this.batchPool.awaitTermination(10, TimeUnit.SECONDS)) {
          this.batchPool.shutdownNow();
        }
      } catch (InterruptedException e) {
        this.batchPool.shutdownNow();
      }
    }
  }
}
