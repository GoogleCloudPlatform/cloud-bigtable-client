package com.google.cloud.anviltop.hbase;

import com.google.api.client.util.Preconditions;
import com.google.bigtable.anviltop.AnviltopData;
import com.google.bigtable.anviltop.AnviltopServiceMessages.AppendRowRequest;
import com.google.bigtable.anviltop.AnviltopServiceMessages.AppendRowResponse;
import com.google.bigtable.anviltop.AnviltopServiceMessages.GetRowRequest;
import com.google.bigtable.anviltop.AnviltopServiceMessages.GetRowResponse;
import com.google.bigtable.anviltop.AnviltopServiceMessages.IncrementRowRequest;
import com.google.bigtable.anviltop.AnviltopServiceMessages.IncrementRowResponse;
import com.google.bigtable.anviltop.AnviltopServiceMessages.MutateRowRequest;
import com.google.bigtable.anviltop.AnviltopServiceMessages.MutateRowResponse;
import com.google.cloud.anviltop.hbase.adapters.AppendAdapter;
import com.google.cloud.anviltop.hbase.adapters.AppendResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.DeleteAdapter;
import com.google.cloud.anviltop.hbase.adapters.GetAdapter;
import com.google.cloud.anviltop.hbase.adapters.GetRowResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.IncrementAdapter;
import com.google.cloud.anviltop.hbase.adapters.IncrementRowResponseAdapter;
import com.google.cloud.anviltop.hbase.adapters.PutAdapter;
import com.google.cloud.anviltop.hbase.adapters.RowMutationsAdapter;
import com.google.cloud.hadoop.hbase.AnviltopClient;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ServiceException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.coprocessor.Batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;

/**
 * Class to help AnviltopTable with batch operations on an AnviltopClient.
 */
public class BatchExecutor {

  protected static final Logger LOG = new Logger(BatchExecutor.class);

  /**
   * For callbacks that take a region, this is the region we will use.
   */
  public static final byte[] NO_REGION = new byte[0];

  /**
   * A callback for ListenableFutures issued as a result of an RPC
   * @param <R>
   * @param <T> The response messsage type.
   */
  static abstract class RpcResultFutureCallback<R, T extends GeneratedMessage>
      implements FutureCallback<T> {

    private final Row row;
    private final Batch.Callback<R> callback;
    private final int index;
    private final Object[] resultsArray;
    private final SettableFuture<Object> resultFuture;

    public RpcResultFutureCallback(
        Row row,
        Batch.Callback<R> callback,
        int index,
        Object[] resultsArray,
        SettableFuture<Object> resultFuture) {
      this.row = row;
      this.callback = callback;
      this.index = index;
      this.resultsArray = resultsArray;
      this.resultFuture = resultFuture;
    }

    /**
     * Adapt a proto result into a client result
     */
    abstract Object adaptResponse(T response);

    @SuppressWarnings("unchecked")
    R unchecked(Object o) {
      return (R)o;
    }

    @Override
    public void onSuccess(T t) {
      try {
        Object result = adaptResponse(t);
        resultsArray[index] = result;
        if (callback != null) {
          callback.update(NO_REGION, row.getRow(), unchecked(result));
        }
        resultFuture.set(result);
      } catch (Throwable throwable) {
        resultFuture.setException(throwable);
      }
    }

    @Override
    public void onFailure(Throwable throwable) {
      try {
        if (callback != null) {
          callback.update(NO_REGION, row.getRow(), null);
        }
      } finally {
        resultsArray[index] = null;
        resultFuture.setException(throwable);
      }
    }
  }

  protected final AnviltopClient client;
  protected final AnviltopOptions options;
  protected final TableName tableName;
  protected final ExecutorService service;
  protected final GetAdapter getAdapter;
  protected final GetRowResponseAdapter getRowResponseAdapter;
  protected final PutAdapter putAdapter;
  protected final DeleteAdapter deleteAdapter;
  protected final RowMutationsAdapter rowMutationsAdapter;
  protected final AppendAdapter appendAdapter;
  protected final AppendResponseAdapter appendRespAdapter;
  protected final IncrementAdapter incrementAdapter;
  protected final IncrementRowResponseAdapter incrRespAdapter;

  public BatchExecutor(
      AnviltopClient client,
      AnviltopOptions options,
      TableName tableName,
      ExecutorService service,
      GetAdapter getAdapter,
      GetRowResponseAdapter getRowResponseAdapter,
      PutAdapter putAdapter,
      DeleteAdapter deleteAdapter,
      RowMutationsAdapter rowMutationsAdapter,
      AppendAdapter appendAdapter,
      AppendResponseAdapter appendRespAdapter,
      IncrementAdapter incrementAdapter,
      IncrementRowResponseAdapter incrRespAdapter) {
    this.client = client;
    this.options = options;
    this.tableName = tableName;
    this.service = service;
    this.getAdapter = getAdapter;
    this.getRowResponseAdapter = getRowResponseAdapter;
    this.putAdapter = putAdapter;
    this.deleteAdapter = deleteAdapter;
    this.rowMutationsAdapter = rowMutationsAdapter;
    this.appendAdapter = appendAdapter;
    this.appendRespAdapter = appendRespAdapter;
    this.incrementAdapter = incrementAdapter;
    this.incrRespAdapter = incrRespAdapter;
  }

  /**
   * Helper to construct a proper MutateRowRequest populated with project, table and mutations.
   */
  MutateRowRequest.Builder makeMutateRowRequest(AnviltopData.RowMutation.Builder mutation) {
    MutateRowRequest.Builder requestBuilder = MutateRowRequest.newBuilder();
    return requestBuilder
        .setMutation(mutation)
        .setTableName(tableName.getQualifierAsString())
        .setProjectId(options.getProjectId());
  }

  /**
   * Adapt and issue a single Delete request returning a ListenableFuture for the MutateRowResponse.
   */
  ListenableFuture<MutateRowResponse> issueDeleteRequest(Delete delete) {
    LOG.trace("issueDeleteRequest(Delete)");
    AnviltopData.RowMutation.Builder mutationBuilder = deleteAdapter.adapt(delete);
    MutateRowRequest.Builder requestBuilder = makeMutateRowRequest(mutationBuilder);

    try {
      return client.mutateAtomicAsync(requestBuilder.build());
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issueDeleteRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Adapt and issue multiple Delete requests returning a list of ListenableFuture instances
   * for the MutateRowResponses.
   */
  List<ListenableFuture<MutateRowResponse>> issueDeleteRequests(
      List<Delete> deletes) {
    LOG.trace("issueDeleteRequests(List<>)");
    List<ListenableFuture<MutateRowResponse>> responseFutures =
        Lists.transform(deletes,
            new Function<Delete,
                ListenableFuture<MutateRowResponse>>() {
              @Override
              public ListenableFuture<MutateRowResponse> apply(Delete delete) {
                return issueDeleteRequest(delete);
              }
            });

    // Force evaluation of the lazy transforms:
    return Lists.newArrayList(responseFutures);
  }

  /**
   * Adapt and issue a single Get request returning a ListenableFuture
   * for the GetRowResponse.
   */
  ListenableFuture<GetRowResponse> issueGetRequest(Get get) {
    LOG.trace("issueGetRequest(Get)");
    GetRowRequest.Builder builder = getAdapter.adapt(get);
    GetRowRequest request = builder
        .setTableName(tableName.getQualifierAsString())
        .setProjectId(options.getProjectId())
        .build();

    try {
      return client.getRowAsync(request);
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issueGetRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Adapt and issue multiple Get requests returning a list of ListenableFuture instances
   * for the GetRowResponses.
   */
  List<ListenableFuture<GetRowResponse>> issueGetRequests(List<Get> gets) {
    LOG.trace("issueGetRequests(List<>)");
    List<ListenableFuture<GetRowResponse>> responseFutures =
        Lists.transform(gets,
            new Function<Get, ListenableFuture<GetRowResponse>>() {
              @Override
              public ListenableFuture<GetRowResponse> apply(Get get) {
                return issueGetRequest(get);
              }
            });
    // Force evaluation of the lazy transforms:
    return Lists.newArrayList(responseFutures);
  }

  /**
   * Adapt and issue a single Append request returning a ListenableFuture
   * for the AppendRowResponse.
   */
  ListenableFuture<AppendRowResponse> issueAppendRequest(
      Append append) {
    LOG.trace("issueAppendRequest(Append)");
    AppendRowRequest request =
        appendAdapter.adapt(append)
            .setTableName(tableName.getQualifierAsString())
            .setProjectId(options.getProjectId())
            .build();

    try {
      return client.appendRowAsync(request);
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issueAppendRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Adapt and issue a single Increment request returning a ListenableFuture
   * for the IncrementRowResponse.
   */
  ListenableFuture<IncrementRowResponse> issueIncrementRequest(
      Increment increment) {
    LOG.trace("issueIncrementRequest(Increment)");
    IncrementRowRequest request =
        incrementAdapter.adapt(increment)
            .setTableName(tableName.getQualifierAsString())
            .setProjectId(options.getProjectId())
            .build();

    try {
      return client.incrementRowAsync(request);
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issueIncrementRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Adapt and issue a single Put request returning a ListenableFuture for the MutateRowResponse.
   */
  ListenableFuture<MutateRowResponse> issuePutRequest(Put put) {
    LOG.trace("issuePutRequest(Put)");
    AnviltopData.RowMutation.Builder mutationBuilder = putAdapter.adapt(put);
    MutateRowRequest.Builder requestBuilder =
        makeMutateRowRequest(mutationBuilder);

    try {
      return client.mutateAtomicAsync(requestBuilder.build());
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issuePutRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Adapt and issue multiple Put requests returning a list of ListenableFuture instances
   * for the MutateRowResponses.
   */
  List<ListenableFuture<MutateRowResponse>> issuePutRequests(List<Put> puts) {
    List<ListenableFuture<MutateRowResponse>> responseFutures =
        Lists.transform(puts,
            new Function<Put,
                ListenableFuture<MutateRowResponse>>() {
              @Override
              public ListenableFuture<MutateRowResponse> apply(Put put) {
                return issuePutRequest(put);
              }
            });
    // Force evaluation of the lazy transforms:
    return Lists.newArrayList(responseFutures);
  }

  /**
   * Adapt and issue a single Put request returning a ListenableFuture for the MutateRowResponse.
   */
  ListenableFuture<MutateRowResponse> issueRowMutationsRequest(
      RowMutations mutations) {
    AnviltopData.RowMutation.Builder mutationBuilder = rowMutationsAdapter.adapt(mutations);
    MutateRowRequest.Builder requestBuilder = makeMutateRowRequest(mutationBuilder);

    try {
      return client.mutateAtomicAsync(requestBuilder.build());
    } catch (ServiceException e) {
      LOG.error("Immediately failing async issueRowMutationsRequest due to ServiceException %s", e);
      return Futures.immediateFailedFuture(e);
    }
  }

  /**
   * Issue a single RPC recording the result into {@code results[index]} and if not-null, invoking
   * the supplied callback.
   * @param row The action to perform
   * @param callback The callback to invoke when the RPC completes and we have results
   * @param results An array of results, into which we should store the result of the operation
   * @param index The into into the array of results where we should store our result
   * @param <R> The action type
   * @param <T> The type of the callback.
   * @return A ListenableFuture that will have the result when the RPC completes.
   */
  <R extends Row,T> ListenableFuture<Object> issueRowRequest(
      final Row row, final Batch.Callback<T> callback, final Object[] results, final int index) {
    LOG.trace("issueRowRequest(Row, Batch.Callback, Object[], index");
    final SettableFuture<Object> resultFuture = SettableFuture.create();
    results[index] = null;
    if (row instanceof Delete) {
      ListenableFuture<MutateRowResponse> rpcResponseFuture =
          issueDeleteRequest((Delete) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, MutateRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(MutateRowResponse response) {
              return new Result();
            }
          },
          service);
    } else  if (row instanceof Get) {
      ListenableFuture<GetRowResponse> rpcResponseFuture =
          issueGetRequest((Get) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, GetRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(GetRowResponse response) {
              return getRowResponseAdapter.adaptResponse(response);
            }
          },
          service);
    } else if (row instanceof  Append) {
      ListenableFuture<AppendRowResponse> rpcResponseFuture =
          issueAppendRequest((Append) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, AppendRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(AppendRowResponse response) {
              return appendRespAdapter.adaptResponse(response);
            }
          },
          service);
    } else if (row instanceof Increment) {
      ListenableFuture<IncrementRowResponse> rpcResponseFuture =
          issueIncrementRequest((Increment) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, IncrementRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(IncrementRowResponse response) {
              return incrRespAdapter.adaptResponse(response);
            }
          },
          service);
    } else if (row instanceof Put) {
      ListenableFuture<MutateRowResponse> rpcResponseFuture =
          issuePutRequest((Put) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, MutateRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(MutateRowResponse response) {
              return new Result();
            }
          },
          service);
    } else if (row instanceof RowMutations) {
      ListenableFuture<MutateRowResponse> rpcResponseFuture =
          issueRowMutationsRequest((RowMutations) row);
      Futures.addCallback(rpcResponseFuture,
          new RpcResultFutureCallback<T, MutateRowResponse>(
              row, callback, index, results, resultFuture) {
            @Override
            Object adaptResponse(MutateRowResponse response) {
              return new Result();
            }
          },
          service);
    } else {
      LOG.error("Encountered unknown action type %s", row.getClass());
      resultFuture.setException(new UnsupportedOperationException(
          String.format("Unknown action type %s", row.getClass().getCanonicalName())));
    }
    return resultFuture;
  }

  /**
   * Implementation of {@link org.apache.hadoop.hbase.client.HTable#batch(List, Object[])}
   */
  public void batch(List<? extends Row> actions, @Nullable Object[] results)
      throws IOException, InterruptedException {
    LOG.trace("batch(List<>, Object[])");
    if (results == null) {
      results = new Object[actions.size()];
    }
    Preconditions.checkArgument(results.length == actions.size(),
        "Result array must have same dimensions as actions list.");
    int index = 0;
    List<ListenableFuture<Object>> resultFutures = new ArrayList<>(actions.size());
    for (Row row : actions) {
      resultFutures.add(issueRowRequest(row, null, results, index++));
    }
    try {
      // Don't want to throw an exception for failed futures, instead the place in results is
      // set to null.
      Futures.successfulAsList(resultFutures).get();
      Iterator<? extends Row> actionIt = actions.iterator();
      Iterator<ListenableFuture<Object>> resultIt = resultFutures.iterator();
      List<Throwable> problems = new ArrayList<Throwable>();
      List<Row> problemActions = new ArrayList<Row>();
      while (actionIt.hasNext() && resultIt.hasNext()) {
        try {
          resultIt.next().get();
          actionIt.next();
        } catch (ExecutionException e) {
          problemActions.add(actionIt.next());
          problems.add(e.getCause());
        }
      }
      if (problems.size() > 0) {
        throw new RetriesExhaustedWithDetailsException(problems, problemActions, new ArrayList<String>(problems.size()));
      }
    } catch (ExecutionException e) {
      LOG.error("Encountered exception in batch(List<>, Object[]). Exception: %s", e);
      throw new IOException("Batch error", e);
    }
  }

  /**
   * Implementation of {@link org.apache.hadoop.hbase.client.HTable#batch(List)}
   */
  public Object[] batch(List<? extends Row> actions) throws IOException {
    LOG.trace("batch(List<>)");
    Result[] results = new Result[actions.size()];
    try {
      batch(actions, results);
    } catch (InterruptedException e) {
      LOG.error("Encountered exception in batch(List<>). Exception: %s", e);
      throw new IOException("Batch error", e);
    }
    return results;
  }

  /**
   * Implementation of
   * {@link org.apache.hadoop.hbase.client.HTable#batchCallback(List, Batch.Callback)}
   */
  public <R> Object[] batchCallback(
      List<? extends Row> actions,
      Batch.Callback<R> callback) throws IOException, InterruptedException {
    LOG.trace("batchCallback(List<>, Batch.Callback)");
    Result[] results = new Result[actions.size()];
    int index = 0;
    List<ListenableFuture<Object>> resultFutures = new ArrayList<>(actions.size());
    for (Row row : actions) {
      resultFutures.add(issueRowRequest(row, callback, results, index++));
    }
    try {
      Futures.allAsList(resultFutures).get();
    } catch (ExecutionException e) {
      LOG.error("Encountered exception in batchCallback(List<>, Batch.Callback). "
          + "Exception: %s", e);
      throw new IOException("batchCallback error", e);
    }
    return results;
  }

  /**
   * Implementation of
   * {@link org.apache.hadoop.hbase.client.HTable#batchCallback(List, Object[], Batch.Callback)}
   */
  public <R> void batchCallback(List<? extends Row> actions,
      Object[] results, Batch.Callback<R> callback) throws IOException, InterruptedException {
    LOG.trace("batchCallback(List<>, Object[], Batch.Callback)");
    Preconditions.checkArgument(results.length == actions.size(),
        "Result array must be the same length as actions.");
    int index = 0;
    List<ListenableFuture<Object>> resultFutures = new ArrayList<>(actions.size());
    for (Row row : actions) {
      resultFutures.add(issueRowRequest(row, callback, results, index++));
    }
    try {
      // Don't want to throw an exception for failed futures, instead the place in results is
      // set to null.
      Futures.successfulAsList(resultFutures).get();
    } catch (ExecutionException e) {
      LOG.error("Encountered exception in batchCallback(List<>, Object[], Batch.Callback). "
          + "Exception: %s", e);
      throw new IOException("batchCallback error", e);
    }
  }

  /**
   * Implementation of {@link org.apache.hadoop.hbase.client.HTable#exists(List)}.
   */
  public Boolean[] exists(List<Get> gets) throws IOException {
    LOG.trace("exists(List<>)");
    // get(gets) will throw if there are any errors:
    Result[] getResults = (Result[]) batch(gets);

    Boolean[] exists = new Boolean[getResults.length];
    for (int index = 0; index < getResults.length; index++) {
      exists[index] = !getResults[index].isEmpty();
    }
    return exists;
  }
}
