// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_instance_admin.proto

package com.google.bigtable.admin.v2;

public interface ListInstancesRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.bigtable.admin.v2.ListInstancesRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The unique name of the project for which a list of instances is requested.
   * Values are of the form `projects/&lt;project&gt;`.
   * </pre>
   *
   * <code>optional string parent = 1;</code>
   */
  java.lang.String getParent();
  /**
   * <pre>
   * The unique name of the project for which a list of instances is requested.
   * Values are of the form `projects/&lt;project&gt;`.
   * </pre>
   *
   * <code>optional string parent = 1;</code>
   */
  com.google.protobuf.ByteString
      getParentBytes();

  /**
   * <pre>
   * The value of `next_page_token` returned by a previous call.
   * </pre>
   *
   * <code>optional string page_token = 2;</code>
   */
  java.lang.String getPageToken();
  /**
   * <pre>
   * The value of `next_page_token` returned by a previous call.
   * </pre>
   *
   * <code>optional string page_token = 2;</code>
   */
  com.google.protobuf.ByteString
      getPageTokenBytes();
}
