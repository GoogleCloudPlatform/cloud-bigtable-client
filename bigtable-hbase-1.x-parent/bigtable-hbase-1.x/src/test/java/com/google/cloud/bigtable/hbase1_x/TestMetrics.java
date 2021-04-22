/*
 * Copyright 2021 Google LLC
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
package com.google.cloud.bigtable.hbase1_x;

import com.google.bigtable.v2.*;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import com.google.cloud.bigtable.metrics.BigtableClientMetrics;
import com.google.cloud.bigtable.metrics.Counter;
import com.google.cloud.bigtable.metrics.Meter;
import com.google.cloud.bigtable.metrics.MetricRegistry;
import com.google.cloud.bigtable.metrics.Timer;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import com.google.common.truth.Truth;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestMetrics {

  private static final String TEST_PROJECT_ID = "fake-project-id";
  private static final String TEST_INSTANCE_ID = "fake-instance-id";
  private static final TableName TABLE_NAME = TableName.valueOf("fake-table");
  private static Server server;
  private static int dataPort;
  private static AtomicInteger callCount = new AtomicInteger(1);
  private FakeMetricRegistry fakeMetricRegistry;

  private MetricRegistry originalMetricRegistry;
  private BigtableClientMetrics.MetricLevel originalLevelToLog;

  private static final FakeDataService fakeDataService = new FakeDataService();
  private BigtableConnection connection;

  private final byte[] rowKey = Bytes.toBytes("row");
  private final byte[] columnFamily = Bytes.toBytes("cf");
  private final byte[] qualifier = Bytes.toBytes("q");
  private final byte[] value = Bytes.toBytes("value");

  @Before
  public void setUp() throws IOException {
    originalLevelToLog = BigtableClientMetrics.getLevelToLog();
    originalMetricRegistry = BigtableClientMetrics.getMetricRegistry(originalLevelToLog);

    Configuration configuration = new Configuration(false);
    configuration.set(BigtableOptionsFactory.PROJECT_ID_KEY, TEST_PROJECT_ID);
    configuration.set(BigtableOptionsFactory.INSTANCE_ID_KEY, TEST_INSTANCE_ID);
    configuration.set(BigtableOptionsFactory.BIGTABLE_NULL_CREDENTIAL_ENABLE_KEY, "true");
    configuration.set(BigtableOptionsFactory.BIGTABLE_DATA_CHANNEL_COUNT_KEY, "1");
    configuration.set(BigtableOptionsFactory.BIGTABLE_EMULATOR_HOST_KEY, "localhost:" + dataPort);
    configuration.set(BigtableOptionsFactory.BIGTABLE_USE_GCJ_CLIENT, "true");
    connection = new BigtableConnection(configuration);

    fakeMetricRegistry = new FakeMetricRegistry();
    BigtableClientMetrics.setMetricRegistry(fakeMetricRegistry);
    BigtableClientMetrics.setLevelToLog(BigtableClientMetrics.MetricLevel.Debug);
  }

  @BeforeClass
  public static void setUpServer() throws IOException {
    try (ServerSocket s = new ServerSocket(0)) {
      dataPort = s.getLocalPort();
    }
    server = ServerBuilder.forPort(dataPort).addService(fakeDataService).build();
    server.start();
  }

  @AfterClass
  public static void tearDownServer() throws InterruptedException {
    if (server != null) {
      server.shutdownNow();
      server.awaitTermination();
    }
  }

  @After
  public void tearDown() throws IOException {
    connection.close();
    BigtableClientMetrics.setMetricRegistry(originalMetricRegistry);
    BigtableClientMetrics.setLevelToLog(originalLevelToLog);
  }

  /*
   * This tests metric instrumentation by using a fake service to inject failures.
   * The fake service will fail the first 3 readrows requests, causing the client to start exponential retries.
   */
  @Test
  public void readRows() throws IOException, InterruptedException {
    Table table = connection.getTable(TABLE_NAME);

    Stopwatch stopwatch = Stopwatch.createStarted();
    Result result = table.get(new Get(new byte[2]));
    long methodInvocationLatency = stopwatch.elapsed(TimeUnit.MILLISECONDS);

    fakeDataService.popLastRequest();

    long readRowRetriesPerformed =
        fakeMetricRegistry
            .results
            .get("google-cloud-bigtable.grpc.method.ReadRow.retries.performed")
            .get();
    long tableGetLatency =
        fakeMetricRegistry.results.get("google-cloud-bigtable.table.get.latency").get();
    long readRowClientOperationLatency =
        fakeMetricRegistry
            .results
            .get("google-cloud-bigtable.grpc.method.ReadRow.operation.latency")
            .get();

    Assert.assertEquals(3, readRowRetriesPerformed);

    Truth.assertThat(tableGetLatency)
        .isIn(Range.closed(FakeDataService.getReadRowServerSideLatency(), methodInvocationLatency));
    Truth.assertThat(readRowClientOperationLatency)
        .isIn(Range.closed(FakeDataService.getReadRowServerSideLatency(), methodInvocationLatency));
  }

  @Test
  public void rowMutations() throws IOException {
    Table table = connection.getTable(TABLE_NAME);

    RowMutations row = new RowMutations(rowKey);
    row.add(new Put(rowKey).addColumn(columnFamily, qualifier, value));

    Stopwatch stopwatch = Stopwatch.createStarted();
    table.mutateRow(row);
    long methodInvocationLatency = stopwatch.elapsed(TimeUnit.MILLISECONDS);

    fakeDataService.popLastRequest();
    long latency =
        fakeMetricRegistry
            .results
            .get("google-cloud-bigtable.grpc.method.MutateRow.rpc.latency")
            .get();
    Truth.assertThat(latency)
        .isIn(
            Range.closed(FakeDataService.getMutateRowServerSideLatency(), methodInvocationLatency));
  }

  @Test
  public void appendFailure() throws IOException {
    Table table = connection.getTable(TABLE_NAME);
    Append append = new Append(rowKey);
    append.add(columnFamily, qualifier, value);
    Stopwatch stopwatch = Stopwatch.createUnstarted();
    try {
      stopwatch.start();
      table.append(append);
      Assert.fail("operation should have failed");
    } catch (Exception e) {
      long methodInvocationLatency = stopwatch.elapsed(TimeUnit.MILLISECONDS);
      fakeDataService.popLastRequest();
      long failureCount =
          fakeMetricRegistry
              .results
              .get("google-cloud-bigtable.grpc.method.ReadModifyWriteRow.failure")
              .get();
      long operationLatency =
          fakeMetricRegistry
              .results
              .get("google-cloud-bigtable.grpc.method.ReadModifyWriteRow.operation.latency")
              .get();

      Assert.assertEquals(1, failureCount);
      Truth.assertThat(operationLatency)
          .isIn(
              Range.closed(
                  FakeDataService.getReadModifyWriteRowServerSideLatency(),
                  methodInvocationLatency));
    }
  }

  private static class FakeDataService extends BigtableGrpc.BigtableImplBase {

    private Stopwatch readRowsStopwatch = Stopwatch.createUnstarted();
    private Stopwatch readModifyWriteRowStopwatch = Stopwatch.createUnstarted();
    private Stopwatch mutateRowStopwatch = Stopwatch.createUnstarted();

    private static long readRowServerSideLatency;
    private static long readModifyWriteRowServerSideLatency;
    private static long mutateRowServerSideLatency;

    final ConcurrentLinkedQueue requests = new ConcurrentLinkedQueue();

    @SuppressWarnings("unchecked")
    <T> T popLastRequest() {
      return (T) requests.poll();
    }

    public static long getReadRowServerSideLatency() {
      return readRowServerSideLatency;
    }

    public static long getMutateRowServerSideLatency() {
      return mutateRowServerSideLatency;
    }

    public static long getReadModifyWriteRowServerSideLatency() {
      return readModifyWriteRowServerSideLatency;
    }

    @Override
    public void readRows(
        ReadRowsRequest request, StreamObserver<ReadRowsResponse> responseObserver) {
      requests.add(request);

      if (!readRowsStopwatch.isRunning()) {
        readRowsStopwatch.start();
      }
      if (callCount.getAndIncrement() < 4) {
        responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE));
      } else {
        try {
          Thread.sleep(20);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        responseObserver.onNext(ReadRowsResponse.newBuilder().build());
        readRowServerSideLatency = readRowsStopwatch.elapsed(TimeUnit.MILLISECONDS);
        responseObserver.onCompleted();
      }
    }

    @Override
    public void readModifyWriteRow(
        ReadModifyWriteRowRequest request,
        StreamObserver<ReadModifyWriteRowResponse> responseObserver) {
      if (!readModifyWriteRowStopwatch.isRunning()) {
        readModifyWriteRowStopwatch.start();
      }
      requests.add(request);
      readModifyWriteRowServerSideLatency =
          readModifyWriteRowStopwatch.elapsed(TimeUnit.MILLISECONDS);
      responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION));
    }

    @Override
    public void mutateRow(
        MutateRowRequest request, StreamObserver<MutateRowResponse> responseObserver) {
      if (!mutateRowStopwatch.isRunning()) {
        mutateRowStopwatch.start();
      }
      requests.add(request);
      responseObserver.onNext(MutateRowResponse.newBuilder().build());
      mutateRowServerSideLatency = mutateRowStopwatch.elapsed(TimeUnit.MILLISECONDS);
      responseObserver.onCompleted();
    }
  }

  private static class FakeMetricRegistry implements MetricRegistry {
    private final Object lock = new Object();

    private final Map<String, AtomicLong> results = new HashMap<>();

    @Override
    public Counter counter(final String name) {
      // counter operations either increment or decrement a key's value
      return new Counter() {
        @Override
        public void inc() {
          synchronized (lock) {
            AtomicLong atomicLong = results.get(name);
            if (atomicLong == null) {
              AtomicLong value = new AtomicLong();
              results.put(name, value);
            }
          }
          results.get(name).getAndIncrement();
        }

        @Override
        public void dec() {
          synchronized (lock) {
            AtomicLong atomicLong = results.get(name);
            if (atomicLong == null) {
              AtomicLong value = new AtomicLong();
              results.put(name, value);
            }
          }
          results.get(name).getAndDecrement();
        }
      };
    }

    @Override
    public Timer timer(final String name) {
      // timer operations overwrite a key's value
      return new Timer() {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        @Override
        public Context time() {
          return new Context() {
            @Override
            public void close() {
              synchronized (lock) {
                results.put(name, new AtomicLong(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
              }
            }
          };
        }

        @Override
        public void update(long duration, TimeUnit unit) {
          // update operations overwrite a key's value
          synchronized (lock) {
            results.put(name, new AtomicLong(duration));
          }
        }
      };
    }

    @Override
    public Meter meter(final String name) {
      // meter operations increment the current key's value
      return new Meter() {
        @Override
        public void mark() {
          synchronized (lock) {
            AtomicLong atomicLong = results.get(name);
            if (atomicLong == null) {
              AtomicLong value = new AtomicLong();
              results.put(name, value);
            }
          }
          results.get(name).getAndIncrement();
        }

        // unless a size is specified, in which case it is overridden
        @Override
        public void mark(long size) {
          synchronized (lock) {
            results.put(name, new AtomicLong(size));
          }
        }
      };
    }
  }
}
