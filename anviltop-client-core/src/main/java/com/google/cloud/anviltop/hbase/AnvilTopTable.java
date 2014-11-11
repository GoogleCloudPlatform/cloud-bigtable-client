/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.anviltop.hbase;

import com.google.bigtable.anviltop.AnviltopData;
import com.google.bigtable.anviltop.AnviltopServices;
import com.google.cloud.anviltop.hbase.adapters.AnviltopResultScannerAdapter;
import com.google.cloud.anviltop.hbase.adapters.AppendAdapter;
import com.google.cloud.anviltop.hbase.adapters.AppendResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.DeleteAdapter;
import com.google.cloud.anviltop.hbase.adapters.FilterAdapter;
import com.google.cloud.anviltop.hbase.adapters.GetAdapter;
import com.google.cloud.anviltop.hbase.adapters.GetRowResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.IncrementAdapter;
import com.google.cloud.anviltop.hbase.adapters.IncrementRowResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.MutationAdapter;
import com.google.cloud.anviltop.hbase.adapters.PutAdapter;
import com.google.cloud.anviltop.hbase.adapters.RowAdapter;
import com.google.cloud.anviltop.hbase.adapters.RowMutationsAdapter;
import com.google.cloud.anviltop.hbase.adapters.ScanAdapter;
import com.google.cloud.anviltop.hbase.adapters.UnsupportedOperationAdapter;
import com.google.cloud.hadoop.hbase.AnviltopClient;
import com.google.cloud.hadoop.hbase.AnviltopResultScanner;
import com.google.cloud.hadoop.hbase.repackaged.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Service;
import com.google.cloud.hadoop.hbase.repackaged.protobuf.ServiceException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class AnvilTopTable implements HTableInterface {
  protected static final Logger LOG = new Logger(AnvilTopTable.class);

  protected final TableName tableName;
  protected final AnviltopOptions options;
  protected final AnviltopClient client;
  protected final RowAdapter rowAdapter = new RowAdapter();
  protected final PutAdapter putAdapter;
  protected final AppendAdapter appendAdapter = new AppendAdapter();
  protected final AppendResponseAdapter appendRespAdapter = new AppendResponseAdapter(rowAdapter);
  protected final IncrementAdapter incrementAdapter = new IncrementAdapter();
  protected final IncrementRowResponseAdapter incrRespAdapter = new IncrementRowResponseAdapter(rowAdapter);
  protected final DeleteAdapter deleteAdapter = new DeleteAdapter();
  protected final MutationAdapter mutationAdapter;
  protected final RowMutationsAdapter rowMutationsAdapter;
  protected final GetRowResponseAdapter getRowResponseAdapter = new GetRowResponseAdapter(rowAdapter);
  protected final ScanAdapter scanAdapter = new ScanAdapter(new FilterAdapter());
  protected final GetAdapter getAdapter = new GetAdapter(scanAdapter);
  protected final AnviltopResultScannerAdapter anviltopResultScannerAdapter =
      new AnviltopResultScannerAdapter(rowAdapter);
  protected final Configuration configuration;
  protected final BatchExecutor batchExecutor;
  private final ExecutorService executorService;

  /**
   * Constructed by AnvilTopConnection
   *
   * @param tableName
   * @param client
   */
  public AnvilTopTable(TableName tableName,
      AnviltopOptions options,
      Configuration configuration,
      AnviltopClient client,
      ExecutorService executorService) {
    LOG.debug("Opening table %s for project %s on host %s and port %s on transport %s",
        tableName.toString(),
        options.getProjectId(),
        options.getTransportOptions().getHost(),
        options.getTransportOptions().getPort(),
        options.getTransportOptions().getTransport());
    this.tableName = tableName;
    this.options = options;
    this.client = client;
    this.configuration = configuration;
    putAdapter = new PutAdapter(configuration);
    mutationAdapter = new MutationAdapter(
        deleteAdapter,
        putAdapter,
        new UnsupportedOperationAdapter<Increment>("increment"),
        new UnsupportedOperationAdapter<Append>("append"));
    rowMutationsAdapter = new RowMutationsAdapter(mutationAdapter);
    this.executorService = executorService;
    this.batchExecutor = new BatchExecutor(
        client,
        options,
        tableName,
        executorService,
        getAdapter,
        getRowResponseAdapter,
        putAdapter,
        deleteAdapter,
        rowMutationsAdapter,
        appendAdapter,
        appendRespAdapter,
        incrementAdapter,
        incrRespAdapter);
  }

  @Override
  public byte[] getTableName() {
    return this.tableName.getName();
  }

  @Override
  public TableName getName() {
    return this.tableName;
  }

  @Override
  public Configuration getConfiguration() {
    return this.configuration;
  }

  @Override
  public HTableDescriptor getTableDescriptor() throws IOException {
    // TODO: Also include column family information
    return new HTableDescriptor(tableName);
  }

  @Override
  public boolean exists(Get get) throws IOException {
    LOG.trace("exists(Get)");
    // TODO: make use of strip_value() or count to hopefully return no extra data
    Result result = get(get);
    return !result.isEmpty();
  }

  @Override
  public boolean[] existsAll(List<Get> gets) throws IOException {
    LOG.trace("existsAll(Get)");
    Boolean[] existsObjects = exists(gets);
    boolean[] exists = new boolean[existsObjects.length];
    for (int i = 0; i < existsObjects.length; i++) {
      exists[i] = existsObjects[i];
    }
    return exists;
  }

  @Override
  public Boolean[] exists(List<Get> gets) throws IOException {
    LOG.trace("exists(List<Get>)");
    return batchExecutor.exists(gets);
  }

  @Override
  public void batch(List<? extends Row> actions, Object[] results)
      throws IOException, InterruptedException {
    LOG.trace("batch(List<>, Object[])");
    batchExecutor.batch(actions, results);
  }

  @Override
  public Object[] batch(List<? extends Row> actions) throws IOException, InterruptedException {
    LOG.trace("batch(List<>)");
    return batchExecutor.batch(actions);
  }

  @Override
  public <R> void batchCallback(List<? extends Row> actions, Object[] results,
      Batch.Callback<R> callback) throws IOException, InterruptedException {
    LOG.trace("batchCallback(List<>, Object[], Batch.Callback)");
    batchExecutor.batchCallback(actions, results, callback);
  }

  @Override
  public <R> Object[] batchCallback(List<? extends Row> actions, Batch.Callback<R> callback)
      throws IOException, InterruptedException {
    LOG.trace("batchCallback(List<>, Batch.Callback)");
    return batchExecutor.batchCallback(actions, callback);
  }

  @Override
  public Result get(Get get) throws IOException {
    LOG.trace("get(Get)");
    AnviltopServices.GetRowRequest.Builder getRowRequest = getAdapter.adapt(get);
    getRowRequest
        .setProjectId(options.getProjectId())
        .setTableName(tableName.getQualifierAsString());

    try {
      AnviltopServices.GetRowResponse response = client.getRow(getRowRequest.build());

      return getRowResponseAdapter.adaptResponse(response);
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing get, Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "get",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              getRowRequest.getRowKey().toByteArray()),
          e);
    }
  }

  @Override
  public Result[] get(List<Get> gets) throws IOException {
    LOG.trace("get(List<>)");
    return (Result[]) batchExecutor.batch(gets);
  }

  @Override
  public Result getRowOrBefore(byte[] row, byte[] family) throws IOException {
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public ResultScanner getScanner(Scan scan) throws IOException {
    LOG.trace("getScanner(Scan)");
    AnviltopServices.ReadTableRequest.Builder request = scanAdapter.adapt(scan);
    request.setProjectId(options.getProjectId());
    request.setTableName(tableName.getQualifierAsString());

    try {
      AnviltopResultScanner scanner = client.readTable(request.build());
      return anviltopResultScannerAdapter.adapt(scanner);
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing getScanner. Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "getScanner",
              options.getProjectId(),
              tableName.getQualifierAsString()),
          e);
    }
  }

  @Override
  public ResultScanner getScanner(byte[] family) throws IOException {
    LOG.trace("getScanner(byte[])");
    return getScanner(new Scan().addFamily(family));
  }

  @Override
  public ResultScanner getScanner(byte[] family, byte[] qualifier) throws IOException {
    LOG.trace("getScanner(byte[], byte[])");
    return getScanner(new Scan().addColumn(family, qualifier));
  }

  @Override
  public void put(Put put) throws IOException {
    LOG.trace("put(Put)");
    AnviltopData.RowMutation.Builder rowMutation = putAdapter.adapt(put);
    AnviltopServices.MutateRowRequest.Builder request = makeMutateRowRequest(rowMutation);

    try {
      client.mutateAtomic(request.build());
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing put. Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "put",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              rowMutation.getRowKey().toByteArray()),
          e);
    }
  }

  @Override
  public void put(List<Put> puts) throws IOException {
    LOG.trace("put(List<Put>)");
    batchExecutor.batch(puts);
  }

  @Override
  public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, byte[] value, Put put)
      throws IOException {
    return checkAndPut(row, family, qualifier, CompareFilter.CompareOp.EQUAL, value, put);
  }

  @Override
  public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier,
      CompareFilter.CompareOp compareOp, byte[] value, Put put) throws IOException {

    AnviltopServices.CheckAndMutateRowRequest.Builder requestBuilder =
        makeConditionalMutationRequestBuilder(
            row, family, qualifier, compareOp, value, put, putAdapter.adapt(put).getModsList());

    try {
      AnviltopServices.CheckAndMutateRowResponse response =
          client.checkAndMutateRow(requestBuilder.build());
      return wasMutationApplied(requestBuilder, response);
    } catch (ServiceException serviceException) {
      throw new IOException(
          makeGenericExceptionMessage(
              "checkAndPut",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              row),
          serviceException);
    }
  }

  @Override
  public void delete(Delete delete) throws IOException {
    LOG.trace("delete(Delete)");
    AnviltopData.RowMutation.Builder rowMutation = deleteAdapter.adapt(delete);
    AnviltopServices.MutateRowRequest.Builder request = makeMutateRowRequest(rowMutation);

    try {
      client.mutateAtomic(request.build());
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing delete. Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "delete",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              rowMutation.getRowKey().toByteArray()),
          e);
    }
  }

  @Override
  public void delete(List<Delete> deletes) throws IOException {
    LOG.trace("delete(List<Delete>)");
    batchExecutor.batch(deletes);
  }

  @Override
  public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, byte[] value,
      Delete delete) throws IOException {
    return checkAndDelete(row, family, qualifier, CompareFilter.CompareOp.EQUAL, value, delete);
  }

  @Override
  public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier,
    CompareFilter.CompareOp compareOp, byte[] value, Delete delete) throws IOException {

    AnviltopServices.CheckAndMutateRowRequest.Builder requestBuilder =
        makeConditionalMutationRequestBuilder(
            row,
            family,
            qualifier,
            compareOp,
            value,
            delete,
            deleteAdapter.adapt(delete).getModsList());

    try {
      AnviltopServices.CheckAndMutateRowResponse response =
          client.checkAndMutateRow(requestBuilder.build());
      return wasMutationApplied(requestBuilder, response);
    } catch (ServiceException serviceException) {
      throw new IOException(
          makeGenericExceptionMessage(
              "checkAndDelete",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              row),
          serviceException);
    }
  }

  @Override
  public void mutateRow(RowMutations rm) throws IOException {
    LOG.trace("mutateRow(RowMutation)");
    AnviltopData.RowMutation.Builder rowMutation = rowMutationsAdapter.adapt(rm);
    AnviltopServices.MutateRowRequest.Builder request = makeMutateRowRequest(rowMutation);

    try {
      client.mutateAtomic(request.build());
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing mutateRow. Exception: %s", e);
      throw new IOException("Failed to mutate.", e);
    }
  }

  @Override
  public Result append(Append append) throws IOException {
    LOG.trace("append(Append)");
    AnviltopServices.AppendRowRequest.Builder appendRowRequest = appendAdapter.adapt(append);
    appendRowRequest
        .setProjectId(options.getProjectId())
        .setTableName(tableName.getQualifierAsString());

    try {
      AnviltopServices.AppendRowResponse response = client.appendRow(appendRowRequest.build());
      return appendRespAdapter.adaptResponse(response);
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing append. Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "append",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              append.getRow()),
          e);
    }
  }

  @Override
  public Result increment(Increment increment) throws IOException {
    LOG.trace("increment(Increment)");
    AnviltopServices.IncrementRowRequest.Builder incrementRowRequest = incrementAdapter.adapt(
        increment);
    incrementRowRequest
        .setProjectId(options.getProjectId())
        .setTableName(tableName.getQualifierAsString());

    try {
      AnviltopServices.IncrementRowResponse response = client.incrementRow(
          incrementRowRequest.build());
      return incrRespAdapter.adaptResponse(response);
    } catch (ServiceException e) {
      LOG.error("Encountered ServiceException when executing increment. Exception: %s", e);
      throw new IOException(
          makeGenericExceptionMessage(
              "increment",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              increment.getRow()),
          e);
    }
  }

  @Override
  public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount)
      throws IOException {
    LOG.trace("incrementColumnValue(byte[], byte[], byte[], long)");
    Increment incr = new Increment(row);
    incr.addColumn(family, qualifier, amount);
    Result result = increment(incr);

    Cell cell = result.getColumnLatestCell(family, qualifier);
    if (cell == null) {
      LOG.error("Failed to find a incremented value in result of increment");
      throw new IOException(
          makeGenericExceptionMessage(
              "increment",
              options.getProjectId(),
              tableName.getQualifierAsString(),
              row));
    }
    return Bytes.toLong(CellUtil.cloneValue(cell));
  }

  @Override
  public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount,
      Durability durability) throws IOException {
    LOG.trace("incrementColumnValue(byte[], byte[], byte[], long, Durability)");
    return incrementColumnValue(row, family, qualifier, amount);
  }

  @Override
  public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount,
      boolean writeToWAL) throws IOException {
    LOG.trace("incrementColumnValue(byte[], byte[], byte[], long, boolean)");
    return incrementColumnValue(row, family, qualifier, amount);
  }

  @Override
  public boolean isAutoFlush() {
    LOG.trace("isAutoFlush()");
    return true;
  }

  @Override
  public void flushCommits() throws IOException {
    LOG.error("Unsupported flushCommits() called.");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public void close() throws IOException {
  }

  @Override
  public CoprocessorRpcChannel coprocessorService(byte[] row) {
    LOG.error("Unsupported coprocessorService(byte[]) called.");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public <T extends Service, R> Map<byte[], R> coprocessorService(Class<T> service, byte[] startKey,
      byte[] endKey, Batch.Call<T, R> callable) throws ServiceException, Throwable {
    LOG.error("Unsupported coprocessorService(Class, byte[], byte[], Batch.Call) called.");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public <T extends Service, R> void coprocessorService(Class<T> service, byte[] startKey,
      byte[] endKey, Batch.Call<T, R> callable, Batch.Callback<R> callback)
      throws ServiceException, Throwable {
    LOG.error("Unsupported coprocessorService("
        + "Class, byte[], byte[], Batch.Call, Batch.Callback) called.");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public void setAutoFlush(boolean autoFlush) {
    LOG.warn("setAutoFlush(%s) invoked, but is currently a NOP", autoFlush);
  }

  @Override
  public void setAutoFlush(boolean autoFlush, boolean clearBufferOnFail) {
    LOG.warn("setAutoFlush(%s, %s), but is currently a NOP", autoFlush, clearBufferOnFail);
  }

  @Override
  public void setAutoFlushTo(boolean autoFlush) {
    LOG.warn("setAutoFlushTo(%s), but is currently a NOP", autoFlush);
  }

  @Override
  public long getWriteBufferSize() {
    LOG.error("Unsupported getWriteBufferSize() called");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public void setWriteBufferSize(long writeBufferSize) throws IOException {
    LOG.error("Unsupported getWriteBufferSize() called");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public <R extends Message> Map<byte[], R> batchCoprocessorService(
      Descriptors.MethodDescriptor methodDescriptor, Message message, byte[] bytes, byte[] bytes2,
      R r) throws ServiceException, Throwable {
    LOG.error("Unsupported batchCoprocessorService("
        + "MethodDescriptor, Message, byte[], byte[], R) called.");
    throw new UnsupportedOperationException();  // TODO
  }

  @Override
  public <R extends Message> void batchCoprocessorService(
      Descriptors.MethodDescriptor methodDescriptor, Message message, byte[] bytes, byte[] bytes2,
      R r, Batch.Callback<R> rCallback) throws ServiceException, Throwable {
    LOG.error("Unsupported batchCoprocessorService("
        + "MethodDescriptor, Message, byte[], byte[], R, Batch.Callback<R>) called.");
    throw new UnsupportedOperationException();  // TODO
  }

  private AnviltopServices.MutateRowRequest.Builder makeMutateRowRequest(
      AnviltopData.RowMutation.Builder rowMutation) {
    LOG.trace("Making mutateRowRequest for table '%s' in project '%s' with mutations '%s'",
        tableName, options.getProjectId(), rowMutation);
    return AnviltopServices.MutateRowRequest.newBuilder()
        .setProjectId(options.getProjectId())
        .setTableName(tableName.getQualifierAsString())
        .setMutation(rowMutation);
  }

  protected boolean wasMutationApplied(
      AnviltopServices.CheckAndMutateRowRequest.Builder requestBuilder,
      AnviltopServices.CheckAndMutateRowResponse response) {

    // If we have true mods, we want the predicate to have matched.
    // If we have false mods, we did not want the predicate to have matched.
    return (requestBuilder.getMutation().getTrueModsCount() > 0
        && response.getPredicateMatched())
        || (requestBuilder.getMutation().getFalseModsCount() > 0
        && !response.getPredicateMatched());
  }

  protected AnviltopServices.CheckAndMutateRowRequest.Builder makeConditionalMutationRequestBuilder(
      byte[] row,
      byte[] family,
      byte[] qualifier,
      CompareFilter.CompareOp compareOp,
      byte[] value,
      Row action,
      List<AnviltopData.RowMutation.Mod> mods) throws IOException {

    if (!Arrays.equals(action.getRow(), row)) {
      throw new DoNotRetryIOException("Action's getRow must match the passed row");
    }

    if (!CompareFilter.CompareOp.EQUAL.equals(compareOp)
        && !CompareFilter.CompareOp.NOT_EQUAL.equals(compareOp)) {
      throw new UnsupportedOperationException(
          String.format(
              "compareOp values other than EQUAL or NOT_EQUAL are not supported in "
                  + "checkAndMutate. Found %s",
              compareOp));
    }

    AnviltopServices.CheckAndMutateRowRequest.Builder requestBuilder =
        AnviltopServices.CheckAndMutateRowRequest.newBuilder();
    requestBuilder.setProjectId(options.getProjectId());
    requestBuilder.setTableName(tableName.getQualifierAsString());

    AnviltopData.ConditionalRowMutation.Builder mutationBuilder =
        requestBuilder.getMutationBuilder();
    mutationBuilder.setRowKey(ByteString.copyFrom(row));
    Scan scan = new Scan().addColumn(family, qualifier);
    scan.setMaxVersions(1);
    if (value == null) {
      // If we don't have a value and we are doing CompareOp.EQUAL, we want to mutate if there
      // is no cell with the qualifier. If we are doing CompareOp.NOT_EQUAL, we want to mutate
      // if there is any cell. We don't actually want an extra filter for either of these cases,
      // but we do need to invert the compare op.
      if (CompareFilter.CompareOp.EQUAL.equals(compareOp)) {
        compareOp = CompareFilter.CompareOp.NOT_EQUAL;
      } else if (CompareFilter.CompareOp.NOT_EQUAL.equals(compareOp)) {
        compareOp = CompareFilter.CompareOp.EQUAL;
      }
    } else {
      scan.setFilter(new ValueFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(value)));
    }
    mutationBuilder.setPredicateFilterBytes(
        ByteString.copyFrom(scanAdapter.buildFilterByteString(scan)));

    if (CompareFilter.CompareOp.EQUAL.equals(compareOp)) {
      mutationBuilder.addAllTrueMods(mods);
    } else if (CompareFilter.CompareOp.NOT_EQUAL.equals(compareOp)) {
      mutationBuilder.addAllFalseMods(mods);
    }

    return requestBuilder;
  }

  static String makeGenericExceptionMessage(String operation, String projectId, String tableName) {
    return String.format(
        "Failed to perform operation. Operation='%s', projectId='%s', tableName='%s'",
        operation,
        projectId,
        tableName);
  }

  static String makeGenericExceptionMessage(
      String operation,
      String projectId,
      String tableName,
      byte[] rowKey) {
    return String.format(
        "Failed to perform operation. Operation='%s', projectId='%s', tableName='%s', rowKey='%s'",
        operation,
        projectId,
        tableName,
        Bytes.toStringBinary(rowKey));
  }
}
