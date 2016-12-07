// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_table_admin.proto

package com.google.bigtable.admin.v2;

public interface SnapshotTableRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.bigtable.admin.v2.SnapshotTableRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The unique name of the table to have the snapshot taken.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
   * </pre>
   *
   * <code>optional string name = 1;</code>
   */
  java.lang.String getName();
  /**
   * <pre>
   * The unique name of the table to have the snapshot taken.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
   * </pre>
   *
   * <code>optional string name = 1;</code>
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * The name of the cluster where the snapshot will be created in.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/clusters/&lt;cluster&gt;`.
   * </pre>
   *
   * <code>optional string cluster = 2;</code>
   */
  java.lang.String getCluster();
  /**
   * <pre>
   * The name of the cluster where the snapshot will be created in.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/clusters/&lt;cluster&gt;`.
   * </pre>
   *
   * <code>optional string cluster = 2;</code>
   */
  com.google.protobuf.ByteString
      getClusterBytes();

  /**
   * <pre>
   * The ID by which the new snapshot should be referred to within the parent
   * cluster, e.g., `mysnapshot` of the form: `[_a-zA-Z0-9][-_.a-zA-Z0-9]*`
   * rather than `projects/&lt;project&gt;/instances/&lt;instance&gt;/clusters/&lt;cluster&gt;/snapshots/mysnapshot`.
   * </pre>
   *
   * <code>optional string snapshot_id = 3;</code>
   */
  java.lang.String getSnapshotId();
  /**
   * <pre>
   * The ID by which the new snapshot should be referred to within the parent
   * cluster, e.g., `mysnapshot` of the form: `[_a-zA-Z0-9][-_.a-zA-Z0-9]*`
   * rather than `projects/&lt;project&gt;/instances/&lt;instance&gt;/clusters/&lt;cluster&gt;/snapshots/mysnapshot`.
   * </pre>
   *
   * <code>optional string snapshot_id = 3;</code>
   */
  com.google.protobuf.ByteString
      getSnapshotIdBytes();

  /**
   * <pre>
   * The amount of time that the new snapshot can stay active after it is
   * created. Once 'ttl' expires, the snapshot will get deleted. The maximum
   * amount of time a snapshot can stay active is 365 days. If 'ttl' is not
   * specified, the default maximum of 365 days will be used.
   * </pre>
   *
   * <code>optional .google.protobuf.Duration ttl = 4;</code>
   */
  boolean hasTtl();
  /**
   * <pre>
   * The amount of time that the new snapshot can stay active after it is
   * created. Once 'ttl' expires, the snapshot will get deleted. The maximum
   * amount of time a snapshot can stay active is 365 days. If 'ttl' is not
   * specified, the default maximum of 365 days will be used.
   * </pre>
   *
   * <code>optional .google.protobuf.Duration ttl = 4;</code>
   */
  com.google.protobuf.Duration getTtl();
  /**
   * <pre>
   * The amount of time that the new snapshot can stay active after it is
   * created. Once 'ttl' expires, the snapshot will get deleted. The maximum
   * amount of time a snapshot can stay active is 365 days. If 'ttl' is not
   * specified, the default maximum of 365 days will be used.
   * </pre>
   *
   * <code>optional .google.protobuf.Duration ttl = 4;</code>
   */
  com.google.protobuf.DurationOrBuilder getTtlOrBuilder();
}
