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
package com.google.cloud.bigtable.grpc;

import static com.google.cloud.bigtable.grpc.io.GoogleCloudResourcePrefixInterceptor.GRPC_RESOURCE_PREFIX_KEY;

import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.core.InternalApi;
import com.google.api.core.NanoClock;
import com.google.bigtable.admin.v2.Backup;
import com.google.bigtable.admin.v2.BigtableTableAdminGrpc;
import com.google.bigtable.admin.v2.CheckConsistencyRequest;
import com.google.bigtable.admin.v2.CheckConsistencyResponse;
import com.google.bigtable.admin.v2.CreateBackupRequest;
import com.google.bigtable.admin.v2.CreateTableFromSnapshotRequest;
import com.google.bigtable.admin.v2.CreateTableRequest;
import com.google.bigtable.admin.v2.DeleteBackupRequest;
import com.google.bigtable.admin.v2.DeleteSnapshotRequest;
import com.google.bigtable.admin.v2.DeleteTableRequest;
import com.google.bigtable.admin.v2.DropRowRangeRequest;
import com.google.bigtable.admin.v2.GenerateConsistencyTokenRequest;
import com.google.bigtable.admin.v2.GenerateConsistencyTokenResponse;
import com.google.bigtable.admin.v2.GetBackupRequest;
import com.google.bigtable.admin.v2.GetSnapshotRequest;
import com.google.bigtable.admin.v2.GetTableRequest;
import com.google.bigtable.admin.v2.ListBackupsRequest;
import com.google.bigtable.admin.v2.ListBackupsResponse;
import com.google.bigtable.admin.v2.ListSnapshotsRequest;
import com.google.bigtable.admin.v2.ListSnapshotsResponse;
import com.google.bigtable.admin.v2.ListTablesRequest;
import com.google.bigtable.admin.v2.ListTablesResponse;
import com.google.bigtable.admin.v2.ModifyColumnFamiliesRequest;
import com.google.bigtable.admin.v2.RestoreTableRequest;
import com.google.bigtable.admin.v2.Snapshot;
import com.google.bigtable.admin.v2.SnapshotTableRequest;
import com.google.bigtable.admin.v2.Table;
import com.google.bigtable.admin.v2.UpdateBackupRequest;
import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.config.RetryOptions;
import com.google.cloud.bigtable.grpc.async.BigtableAsyncRpc;
import com.google.cloud.bigtable.grpc.async.BigtableAsyncUtilities;
import com.google.cloud.bigtable.grpc.async.RetryingUnaryOperation;
import com.google.cloud.bigtable.util.OperationUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.longrunning.GetOperationRequest;
import com.google.longrunning.Operation;
import com.google.longrunning.OperationsGrpc;
import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.Metadata;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A gRPC client for accessing the Bigtable Table Admin API.
 *
 * <p>For internal use only - public for technical reasons.
 */
@InternalApi("For internal usage only")
public class BigtableTableAdminGrpcClient implements BigtableTableAdminClient {
  private final DeadlineGeneratorFactory deadlineGeneratorFactory =
      DeadlineGeneratorFactory.DEFAULT;

  private final OperationUtil operationUtil;

  private final BigtableAsyncRpc<ListTablesRequest, ListTablesResponse> listTablesRpc;
  private final RetryOptions retryOptions;
  private final ScheduledExecutorService retryExecutorService;
  private final BigtableAsyncRpc<GetTableRequest, Table> getTableRpc;
  private final BigtableAsyncRpc<CreateTableRequest, Table> createTableRpc;
  private final BigtableAsyncRpc<ModifyColumnFamiliesRequest, Table> modifyColumnFamilyRpc;
  private final BigtableAsyncRpc<DeleteTableRequest, Empty> deleteTableRpc;
  private final BigtableAsyncRpc<DropRowRangeRequest, Empty> dropRowRangeRpc;
  private final BigtableAsyncRpc<GenerateConsistencyTokenRequest, GenerateConsistencyTokenResponse>
      generateConsistencyTokenRpc;
  private final BigtableAsyncRpc<CheckConsistencyRequest, CheckConsistencyResponse>
      checkConsistencyRpc;
  private final BigtableAsyncRpc<GetIamPolicyRequest, Policy> getIamPolicyRpc;
  private final BigtableAsyncRpc<SetIamPolicyRequest, Policy> setIamPolicyRpc;
  private final BigtableAsyncRpc<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testIamPermissionsRpc;

  private final BigtableAsyncRpc<SnapshotTableRequest, Operation> snapshotTableRpc;
  private final BigtableAsyncRpc<GetSnapshotRequest, Snapshot> getSnapshotRpc;
  private final BigtableAsyncRpc<ListSnapshotsRequest, ListSnapshotsResponse> listSnapshotsRpc;
  private final BigtableAsyncRpc<DeleteSnapshotRequest, Empty> deleteSnapshotRpc;
  private final BigtableAsyncRpc<CreateTableFromSnapshotRequest, Operation>
      createTableFromSnapshotRpc;

  private final BigtableAsyncRpc<GetBackupRequest, Backup> getBackupRpc;
  private final BigtableAsyncRpc<ListBackupsRequest, ListBackupsResponse> listBackupRpc;
  private final BigtableAsyncRpc<CreateBackupRequest, Operation> createBackupRpc;
  private final BigtableAsyncRpc<UpdateBackupRequest, Backup> updateBackupRpc;
  private final BigtableAsyncRpc<DeleteBackupRequest, Empty> deleteBackupRpc;
  private final BigtableAsyncRpc<RestoreTableRequest, Operation> restoreTableRpc;

  /**
   * Constructor for BigtableTableAdminGrpcClient.
   *
   * @param channel a {@link io.grpc.Channel} object.
   */
  public BigtableTableAdminGrpcClient(
      Channel channel,
      ScheduledExecutorService retryExecutorService,
      BigtableOptions bigtableOptions) {
    BigtableAsyncUtilities asyncUtilities = new BigtableAsyncUtilities.Default(channel);

    operationUtil = new OperationUtil(OperationsGrpc.newBlockingStub(channel));

    // Read only methods.  These are always retried.
    this.listTablesRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getListTablesMethod(),
            Predicates.<ListTablesRequest>alwaysTrue());
    this.getTableRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getGetTableMethod(), Predicates.<GetTableRequest>alwaysTrue());

    this.listBackupRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getListBackupsMethod(),
            Predicates.<ListBackupsRequest>alwaysTrue());
    this.getBackupRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getGetBackupMethod(), Predicates.<GetBackupRequest>alwaysTrue());

    // Write methods. These are only retried for UNAVAILABLE or UNAUTHORIZED
    this.createTableRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getCreateTableMethod(),
            Predicates.<CreateTableRequest>alwaysFalse());
    this.modifyColumnFamilyRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getModifyColumnFamiliesMethod(),
            Predicates.<ModifyColumnFamiliesRequest>alwaysFalse());
    this.deleteTableRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getDeleteTableMethod(),
            Predicates.<DeleteTableRequest>alwaysFalse());
    this.dropRowRangeRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getDropRowRangeMethod(),
            Predicates.<DropRowRangeRequest>alwaysFalse());
    this.generateConsistencyTokenRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getGenerateConsistencyTokenMethod(),
            Predicates.<GenerateConsistencyTokenRequest>alwaysFalse());
    this.checkConsistencyRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getCheckConsistencyMethod(),
            Predicates.<CheckConsistencyRequest>alwaysFalse());
    this.getIamPolicyRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getGetIamPolicyMethod(),
            Predicates.<GetIamPolicyRequest>alwaysFalse());
    this.setIamPolicyRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getSetIamPolicyMethod(),
            Predicates.<SetIamPolicyRequest>alwaysFalse());
    this.testIamPermissionsRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getTestIamPermissionsMethod(),
            Predicates.<TestIamPermissionsRequest>alwaysFalse());

    this.snapshotTableRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getSnapshotTableMethod(),
            Predicates.<SnapshotTableRequest>alwaysFalse());
    this.getSnapshotRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getGetSnapshotMethod(),
            Predicates.<GetSnapshotRequest>alwaysTrue());
    this.listSnapshotsRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getListSnapshotsMethod(),
            Predicates.<ListSnapshotsRequest>alwaysTrue());
    this.deleteSnapshotRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getDeleteSnapshotMethod(),
            Predicates.<DeleteSnapshotRequest>alwaysFalse());
    this.createTableFromSnapshotRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getCreateTableFromSnapshotMethod(),
            Predicates.<CreateTableFromSnapshotRequest>alwaysFalse());

    this.createBackupRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getCreateBackupMethod(),
            Predicates.<CreateBackupRequest>alwaysFalse());
    this.updateBackupRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getUpdateBackupMethod(),
            Predicates.<UpdateBackupRequest>alwaysFalse());
    this.deleteBackupRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getDeleteBackupMethod(),
            Predicates.<DeleteBackupRequest>alwaysFalse());
    this.restoreTableRpc =
        asyncUtilities.createAsyncRpc(
            BigtableTableAdminGrpc.getRestoreTableMethod(),
            Predicates.<RestoreTableRequest>alwaysFalse());

    this.retryOptions = bigtableOptions.getRetryOptions();
    this.retryExecutorService = retryExecutorService;
  }

  /** {@inheritDoc} */
  @Override
  public ListTablesResponse listTables(ListTablesRequest request) {
    return createUnaryListener(request, listTablesRpc, request.getParent()).getBlockingResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<ListTablesResponse> listTablesAsync(ListTablesRequest request) {
    return createUnaryListener(request, listTablesRpc, request.getParent()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public Table getTable(GetTableRequest request) {
    return createUnaryListener(request, getTableRpc, request.getName()).getBlockingResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<Table> getTableAsync(GetTableRequest request) {
    return createUnaryListener(request, getTableRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public Table createTable(CreateTableRequest request) {
    return createUnaryListener(request, createTableRpc, request.getParent()).getBlockingResult();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public ListenableFuture<Table> createTableAsync(CreateTableRequest request) {
    return createUnaryListener(request, createTableRpc, request.getParent()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public Table modifyColumnFamily(ModifyColumnFamiliesRequest request) {
    return createUnaryListener(request, modifyColumnFamilyRpc, request.getName())
        .getBlockingResult();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public ListenableFuture<Table> modifyColumnFamilyAsync(ModifyColumnFamiliesRequest request) {
    return createUnaryListener(request, modifyColumnFamilyRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public void deleteTable(DeleteTableRequest request) {
    createUnaryListener(request, deleteTableRpc, request.getName()).getBlockingResult();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public ListenableFuture<Empty> deleteTableAsync(DeleteTableRequest request) {
    return createUnaryListener(request, deleteTableRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public void dropRowRange(DropRowRangeRequest request) {
    createUnaryListener(request, dropRowRangeRpc, request.getName()).getBlockingResult();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public ListenableFuture<Empty> dropRowRangeAsync(DropRowRangeRequest request) {
    return createUnaryListener(request, dropRowRangeRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public void waitForReplication(BigtableTableName tableName, long timeout)
      throws InterruptedException, TimeoutException {
    // A backoff that randomizes with an interval of 10s.
    ExponentialBackOff backOff =
        new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(10 * 1000)
            .setMaxIntervalMillis(10 * 1000)
            .setMaxElapsedTimeMillis(Ints.checkedCast(timeout * 1000))
            .build();

    waitForReplication(tableName, backOff);
  }

  /** {@inheritDoc} */
  @Override
  public Policy getIamPolicy(GetIamPolicyRequest request) {
    return createUnaryListener(request, getIamPolicyRpc, request.getResource()).getBlockingResult();
  }

  /** {@inheritDoc} */
  @Override
  public Policy setIamPolicy(SetIamPolicyRequest request) {
    return createUnaryListener(request, setIamPolicyRpc, request.getResource()).getBlockingResult();
  }

  /** {@inheritDoc} */
  @Override
  public TestIamPermissionsResponse testIamPermissions(TestIamPermissionsRequest request) {
    return createUnaryListener(request, testIamPermissionsRpc, request.getResource())
        .getBlockingResult();
  }

  @Override
  public ListenableFuture<Operation> createBackupAsync(CreateBackupRequest request) {
    return createUnaryListener(request, createBackupRpc, request.getParent()).getAsyncResult();
  }

  @Override
  public ListenableFuture<Backup> getBackupAsync(GetBackupRequest request) {
    return createUnaryListener(request, getBackupRpc, request.getName()).getAsyncResult();
  }

  @Override
  public ListenableFuture<Backup> updateBackupAsync(UpdateBackupRequest request) {
    return createUnaryListener(request, updateBackupRpc, request.getBackup().getName())
        .getAsyncResult();
  }

  @Override
  public ListenableFuture<ListBackupsResponse> listBackupsAsync(ListBackupsRequest request) {
    return createUnaryListener(request, listBackupRpc, request.getParent()).getAsyncResult();
  }

  @Override
  public ListenableFuture<Empty> deleteBackupAsync(DeleteBackupRequest request) {
    return createUnaryListener(request, deleteBackupRpc, request.getName()).getAsyncResult();
  }

  @Override
  public ListenableFuture<Operation> restoreTableAsync(RestoreTableRequest request) {
    return createUnaryListener(request, restoreTableRpc, request.getParent()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public Operation getOperation(GetOperationRequest request) {
    return operationUtil.getOperation(request);
  }

  /** {@inheritDoc} */
  @Override
  public Operation waitForOperation(Operation operation) throws IOException, TimeoutException {
    return operationUtil.waitForOperation(operation, 10, TimeUnit.MINUTES);
  }

  /** {@inheritDoc} */
  @Override
  public Operation waitForOperation(Operation operation, long timeout, TimeUnit timeUnit)
      throws TimeoutException, IOException {
    return operationUtil.waitForOperation(operation, timeout, timeUnit);
  }

  @VisibleForTesting
  void waitForReplication(BigtableTableName tableName, BackOff backOff)
      throws InterruptedException, TimeoutException {
    String token = generateConsistencyToken(tableName);

    while (!checkConsistency(tableName, token)) {
      long backOffMillis;
      try {
        backOffMillis = backOff.nextBackOffMillis();
      } catch (IOException e) {
        // Should never happen, we only use ExponentialBackOff which doesn't throw.
        throw new RuntimeException("Problem getting backoff: " + e);
      }
      if (backOffMillis == BackOff.STOP) {
        throw new TimeoutException(
            "Table " + tableName.toString() + " is not consistent after timeout.");

      } else {
        // sleep for backOffMillis milliseconds and retry operation.
        Thread.sleep(backOffMillis);
      }
    }
  }

  private String generateConsistencyToken(BigtableTableName tableName) {
    GenerateConsistencyTokenRequest request =
        GenerateConsistencyTokenRequest.newBuilder().setName(tableName.toString()).build();

    return createUnaryListener(request, generateConsistencyTokenRpc, request.getName())
        .getBlockingResult()
        .getConsistencyToken();
  }

  private boolean checkConsistency(BigtableTableName tableName, String token) {
    CheckConsistencyRequest request =
        CheckConsistencyRequest.newBuilder()
            .setName(tableName.toString())
            .setConsistencyToken(token)
            .build();

    return createUnaryListener(request, checkConsistencyRpc, request.getName())
        .getBlockingResult()
        .getConsistent();
  }

  private <ReqT, RespT> RetryingUnaryOperation<ReqT, RespT> createUnaryListener(
      ReqT request, BigtableAsyncRpc<ReqT, RespT> rpc, String resource) {
    Metadata metadata = createMetadata(resource);
    return new RetryingUnaryOperation<>(
        retryOptions,
        request,
        rpc,
        deadlineGeneratorFactory.getRequestDeadlineGenerator(request, rpc.isRetryable(request)),
        retryExecutorService,
        metadata,
        NanoClock.getDefaultClock());
  }

  /** Creates a {@link Metadata} that contains pertinent headers. */
  private Metadata createMetadata(String resource) {
    Metadata metadata = new Metadata();
    if (resource != null) {
      metadata.put(GRPC_RESOURCE_PREFIX_KEY, resource);
    }
    return metadata;
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<Operation> snapshotTableAsync(SnapshotTableRequest request) {
    return createUnaryListener(request, snapshotTableRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<Snapshot> getSnapshotAsync(GetSnapshotRequest request) {
    return createUnaryListener(request, getSnapshotRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<ListSnapshotsResponse> listSnapshotsAsync(ListSnapshotsRequest request) {
    return createUnaryListener(request, listSnapshotsRpc, request.getParent()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<Empty> deleteSnapshotAsync(DeleteSnapshotRequest request) {
    return createUnaryListener(request, deleteSnapshotRpc, request.getName()).getAsyncResult();
  }

  /** {@inheritDoc} */
  @Override
  public ListenableFuture<Operation> createTableFromSnapshotAsync(
      CreateTableFromSnapshotRequest request) {
    return createUnaryListener(request, createTableFromSnapshotRpc, request.getParent())
        .getAsyncResult();
  }
}
