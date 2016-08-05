// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_instance_admin.proto

package com.google.bigtable.admin.v2;

public final class BigtableInstanceAdminProto {
  private BigtableInstanceAdminProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateInstanceRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_ClustersEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateInstanceRequest_ClustersEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_GetInstanceRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_GetInstanceRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListInstancesRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListInstancesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListInstancesResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListInstancesResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_DeleteInstanceRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_DeleteInstanceRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateClusterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateClusterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_GetClusterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_GetClusterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListClustersRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListClustersRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListClustersResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListClustersResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_DeleteClusterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_DeleteClusterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateInstanceMetadata_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateInstanceMetadata_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_UpdateClusterMetadata_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_UpdateClusterMetadata_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n6google/bigtable/admin/v2/bigtable_inst" +
      "ance_admin.proto\022\030google.bigtable.admin." +
      "v2\032\034google/api/annotations.proto\032\'google" +
      "/bigtable/admin/v2/instance.proto\032#googl" +
      "e/longrunning/operations.proto\032\033google/p" +
      "rotobuf/empty.proto\032\037google/protobuf/tim" +
      "estamp.proto\"\227\002\n\025CreateInstanceRequest\022\016" +
      "\n\006parent\030\001 \001(\t\022\023\n\013instance_id\030\002 \001(\t\0224\n\010i" +
      "nstance\030\003 \001(\0132\".google.bigtable.admin.v2" +
      ".Instance\022O\n\010clusters\030\004 \003(\0132=.google.big",
      "table.admin.v2.CreateInstanceRequest.Clu" +
      "stersEntry\032R\n\rClustersEntry\022\013\n\003key\030\001 \001(\t" +
      "\0220\n\005value\030\002 \001(\0132!.google.bigtable.admin." +
      "v2.Cluster:\0028\001\"\"\n\022GetInstanceRequest\022\014\n\004" +
      "name\030\001 \001(\t\":\n\024ListInstancesRequest\022\016\n\006pa" +
      "rent\030\001 \001(\t\022\022\n\npage_token\030\002 \001(\t\"\201\001\n\025ListI" +
      "nstancesResponse\0225\n\tinstances\030\001 \003(\0132\".go" +
      "ogle.bigtable.admin.v2.Instance\022\030\n\020faile" +
      "d_locations\030\002 \003(\t\022\027\n\017next_page_token\030\003 \001" +
      "(\t\"%\n\025DeleteInstanceRequest\022\014\n\004name\030\001 \001(",
      "\t\"n\n\024CreateClusterRequest\022\016\n\006parent\030\001 \001(" +
      "\t\022\022\n\ncluster_id\030\002 \001(\t\0222\n\007cluster\030\003 \001(\0132!" +
      ".google.bigtable.admin.v2.Cluster\"!\n\021Get" +
      "ClusterRequest\022\014\n\004name\030\001 \001(\t\"9\n\023ListClus" +
      "tersRequest\022\016\n\006parent\030\001 \001(\t\022\022\n\npage_toke" +
      "n\030\002 \001(\t\"~\n\024ListClustersResponse\0223\n\010clust" +
      "ers\030\001 \003(\0132!.google.bigtable.admin.v2.Clu" +
      "ster\022\030\n\020failed_locations\030\002 \003(\t\022\027\n\017next_p" +
      "age_token\030\003 \001(\t\"$\n\024DeleteClusterRequest\022" +
      "\014\n\004name\030\001 \001(\t\"\306\001\n\026CreateInstanceMetadata",
      "\022I\n\020original_request\030\001 \001(\0132/.google.bigt" +
      "able.admin.v2.CreateInstanceRequest\0220\n\014r" +
      "equest_time\030\002 \001(\0132\032.google.protobuf.Time" +
      "stamp\022/\n\013finish_time\030\003 \001(\0132\032.google.prot" +
      "obuf.Timestamp\"\267\001\n\025UpdateClusterMetadata" +
      "\022;\n\020original_request\030\001 \001(\0132!.google.bigt" +
      "able.admin.v2.Cluster\0220\n\014request_time\030\002 " +
      "\001(\0132\032.google.protobuf.Timestamp\022/\n\013finis" +
      "h_time\030\003 \001(\0132\032.google.protobuf.Timestamp" +
      "2\333\013\n\025BigtableInstanceAdmin\022\216\001\n\016CreateIns",
      "tance\022/.google.bigtable.admin.v2.CreateI" +
      "nstanceRequest\032\035.google.longrunning.Oper" +
      "ation\",\202\323\344\223\002&\"!/v2/{parent=projects/*}/i" +
      "nstances:\001*\022\212\001\n\013GetInstance\022,.google.big" +
      "table.admin.v2.GetInstanceRequest\032\".goog" +
      "le.bigtable.admin.v2.Instance\")\202\323\344\223\002#\022!/" +
      "v2/{name=projects/*/instances/*}\022\233\001\n\rLis" +
      "tInstances\022..google.bigtable.admin.v2.Li" +
      "stInstancesRequest\032/.google.bigtable.adm" +
      "in.v2.ListInstancesResponse\")\202\323\344\223\002#\022!/v2",
      "/{parent=projects/*}/instances\022\206\001\n\016Updat" +
      "eInstance\022\".google.bigtable.admin.v2.Ins" +
      "tance\032\".google.bigtable.admin.v2.Instanc" +
      "e\",\202\323\344\223\002&\032!/v2/{name=projects/*/instance" +
      "s/*}:\001*\022\204\001\n\016DeleteInstance\022/.google.bigt" +
      "able.admin.v2.DeleteInstanceRequest\032\026.go" +
      "ogle.protobuf.Empty\")\202\323\344\223\002#*!/v2/{name=p" +
      "rojects/*/instances/*}\022\235\001\n\rCreateCluster" +
      "\022..google.bigtable.admin.v2.CreateCluste" +
      "rRequest\032\035.google.longrunning.Operation\"",
      "=\202\323\344\223\0027\",/v2/{parent=projects/*/instance" +
      "s/*}/clusters:\007cluster\022\222\001\n\nGetCluster\022+." +
      "google.bigtable.admin.v2.GetClusterReque" +
      "st\032!.google.bigtable.admin.v2.Cluster\"4\202" +
      "\323\344\223\002.\022,/v2/{name=projects/*/instances/*/" +
      "clusters/*}\022\243\001\n\014ListClusters\022-.google.bi" +
      "gtable.admin.v2.ListClustersRequest\032..go" +
      "ogle.bigtable.admin.v2.ListClustersRespo" +
      "nse\"4\202\323\344\223\002.\022,/v2/{parent=projects/*/inst" +
      "ances/*}/clusters\022\212\001\n\rUpdateCluster\022!.go",
      "ogle.bigtable.admin.v2.Cluster\032\035.google." +
      "longrunning.Operation\"7\202\323\344\223\0021\032,/v2/{name" +
      "=projects/*/instances/*/clusters/*}:\001*\022\215" +
      "\001\n\rDeleteCluster\022..google.bigtable.admin" +
      ".v2.DeleteClusterRequest\032\026.google.protob" +
      "uf.Empty\"4\202\323\344\223\002.*,/v2/{name=projects/*/i" +
      "nstances/*/clusters/*}B<\n\034com.google.big" +
      "table.admin.v2B\032BigtableInstanceAdminPro" +
      "toP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.google.bigtable.admin.v2.InstanceProto.getDescriptor(),
          com.google.longrunning.OperationsProto.getDescriptor(),
          com.google.protobuf.EmptyProto.getDescriptor(),
          com.google.protobuf.TimestampProto.getDescriptor(),
        }, assigner);
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateInstanceRequest_descriptor,
        new java.lang.String[] { "Parent", "InstanceId", "Instance", "Clusters", });
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_ClustersEntry_descriptor =
      internal_static_google_bigtable_admin_v2_CreateInstanceRequest_descriptor.getNestedTypes().get(0);
    internal_static_google_bigtable_admin_v2_CreateInstanceRequest_ClustersEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateInstanceRequest_ClustersEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_google_bigtable_admin_v2_GetInstanceRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_bigtable_admin_v2_GetInstanceRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_GetInstanceRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_ListInstancesRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_google_bigtable_admin_v2_ListInstancesRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListInstancesRequest_descriptor,
        new java.lang.String[] { "Parent", "PageToken", });
    internal_static_google_bigtable_admin_v2_ListInstancesResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_google_bigtable_admin_v2_ListInstancesResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListInstancesResponse_descriptor,
        new java.lang.String[] { "Instances", "FailedLocations", "NextPageToken", });
    internal_static_google_bigtable_admin_v2_DeleteInstanceRequest_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_google_bigtable_admin_v2_DeleteInstanceRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_DeleteInstanceRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_CreateClusterRequest_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_google_bigtable_admin_v2_CreateClusterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateClusterRequest_descriptor,
        new java.lang.String[] { "Parent", "ClusterId", "Cluster", });
    internal_static_google_bigtable_admin_v2_GetClusterRequest_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_google_bigtable_admin_v2_GetClusterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_GetClusterRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_ListClustersRequest_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_google_bigtable_admin_v2_ListClustersRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListClustersRequest_descriptor,
        new java.lang.String[] { "Parent", "PageToken", });
    internal_static_google_bigtable_admin_v2_ListClustersResponse_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_google_bigtable_admin_v2_ListClustersResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListClustersResponse_descriptor,
        new java.lang.String[] { "Clusters", "FailedLocations", "NextPageToken", });
    internal_static_google_bigtable_admin_v2_DeleteClusterRequest_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_google_bigtable_admin_v2_DeleteClusterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_DeleteClusterRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_CreateInstanceMetadata_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_google_bigtable_admin_v2_CreateInstanceMetadata_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateInstanceMetadata_descriptor,
        new java.lang.String[] { "OriginalRequest", "RequestTime", "FinishTime", });
    internal_static_google_bigtable_admin_v2_UpdateClusterMetadata_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_google_bigtable_admin_v2_UpdateClusterMetadata_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_UpdateClusterMetadata_descriptor,
        new java.lang.String[] { "OriginalRequest", "RequestTime", "FinishTime", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.bigtable.admin.v2.InstanceProto.getDescriptor();
    com.google.longrunning.OperationsProto.getDescriptor();
    com.google.protobuf.EmptyProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
