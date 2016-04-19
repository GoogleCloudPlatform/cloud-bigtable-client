// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_instance_admin.proto

package com.google.bigtable.admin.v2;

public interface ListInstancesResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.bigtable.admin.v2.ListInstancesResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .google.bigtable.admin.v2.Instance instances = 1;</code>
   */
  java.util.List<com.google.bigtable.admin.v2.Instance> 
      getInstancesList();
  /**
   * <code>repeated .google.bigtable.admin.v2.Instance instances = 1;</code>
   */
  com.google.bigtable.admin.v2.Instance getInstances(int index);
  /**
   * <code>repeated .google.bigtable.admin.v2.Instance instances = 1;</code>
   */
  int getInstancesCount();
  /**
   * <code>repeated .google.bigtable.admin.v2.Instance instances = 1;</code>
   */
  java.util.List<? extends com.google.bigtable.admin.v2.InstanceOrBuilder> 
      getInstancesOrBuilderList();
  /**
   * <code>repeated .google.bigtable.admin.v2.Instance instances = 1;</code>
   */
  com.google.bigtable.admin.v2.InstanceOrBuilder getInstancesOrBuilder(
      int index);

  /**
   * <code>optional string next_page_token = 2;</code>
   */
  java.lang.String getNextPageToken();
  /**
   * <code>optional string next_page_token = 2;</code>
   */
  com.google.protobuf.ByteString
      getNextPageTokenBytes();
}
