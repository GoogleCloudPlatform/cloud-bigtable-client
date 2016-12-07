// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_table_admin.proto

package com.google.bigtable.admin.v2;

public final class BigtableTableAdminProto {
  private BigtableTableAdminProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateTableRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateTableRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateTableRequest_Split_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateTableRequest_Split_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_CreateTableFromSnapshotRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_CreateTableFromSnapshotRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_DropRowRangeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListTablesRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListTablesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListTablesResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListTablesResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_GetTableRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_GetTableRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_DeleteTableRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_DeleteTableRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_Modification_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_Modification_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_SnapshotTableRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_SnapshotTableRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_GetSnapshotRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_GetSnapshotRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListSnapshotsRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListSnapshotsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_ListSnapshotsResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_ListSnapshotsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_bigtable_admin_v2_DeleteSnapshotRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_bigtable_admin_v2_DeleteSnapshotRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n3google/bigtable/admin/v2/bigtable_tabl" +
      "e_admin.proto\022\030google.bigtable.admin.v2\032" +
      "\034google/api/annotations.proto\032$google/bi" +
      "gtable/admin/v2/table.proto\032#google/long" +
      "running/operations.proto\032\036google/protobu" +
      "f/duration.proto\032\033google/protobuf/empty." +
      "proto\"\310\001\n\022CreateTableRequest\022\016\n\006parent\030\001" +
      " \001(\t\022\020\n\010table_id\030\002 \001(\t\022.\n\005table\030\003 \001(\0132\037." +
      "google.bigtable.admin.v2.Table\022J\n\016initia" +
      "l_splits\030\004 \003(\01322.google.bigtable.admin.v",
      "2.CreateTableRequest.Split\032\024\n\005Split\022\013\n\003k" +
      "ey\030\001 \001(\014\"[\n\036CreateTableFromSnapshotReque" +
      "st\022\016\n\006parent\030\001 \001(\t\022\020\n\010table_id\030\002 \001(\t\022\027\n\017" +
      "source_snapshot\030\003 \001(\t\"m\n\023DropRowRangeReq" +
      "uest\022\014\n\004name\030\001 \001(\t\022\030\n\016row_key_prefix\030\002 \001" +
      "(\014H\000\022$\n\032delete_all_data_from_table\030\003 \001(\010" +
      "H\000B\010\n\006target\"k\n\021ListTablesRequest\022\016\n\006par" +
      "ent\030\001 \001(\t\0222\n\004view\030\002 \001(\0162$.google.bigtabl" +
      "e.admin.v2.Table.View\022\022\n\npage_token\030\003 \001(" +
      "\t\"^\n\022ListTablesResponse\022/\n\006tables\030\001 \003(\0132",
      "\037.google.bigtable.admin.v2.Table\022\027\n\017next" +
      "_page_token\030\002 \001(\t\"S\n\017GetTableRequest\022\014\n\004" +
      "name\030\001 \001(\t\0222\n\004view\030\002 \001(\0162$.google.bigtab" +
      "le.admin.v2.Table.View\"\"\n\022DeleteTableReq" +
      "uest\022\014\n\004name\030\001 \001(\t\"\256\002\n\033ModifyColumnFamil" +
      "iesRequest\022\014\n\004name\030\001 \001(\t\022Y\n\rmodification" +
      "s\030\002 \003(\0132B.google.bigtable.admin.v2.Modif" +
      "yColumnFamiliesRequest.Modification\032\245\001\n\014" +
      "Modification\022\n\n\002id\030\001 \001(\t\0228\n\006create\030\002 \001(\013" +
      "2&.google.bigtable.admin.v2.ColumnFamily",
      "H\000\0228\n\006update\030\003 \001(\0132&.google.bigtable.adm" +
      "in.v2.ColumnFamilyH\000\022\016\n\004drop\030\004 \001(\010H\000B\005\n\003" +
      "mod\"r\n\024SnapshotTableRequest\022\014\n\004name\030\001 \001(" +
      "\t\022\017\n\007cluster\030\002 \001(\t\022\023\n\013snapshot_id\030\003 \001(\t\022" +
      "&\n\003ttl\030\004 \001(\0132\031.google.protobuf.Duration\"" +
      "\"\n\022GetSnapshotRequest\022\014\n\004name\030\001 \001(\t\"M\n\024L" +
      "istSnapshotsRequest\022\016\n\006parent\030\001 \001(\t\022\021\n\tp" +
      "age_size\030\002 \001(\005\022\022\n\npage_token\030\003 \001(\t\"g\n\025Li" +
      "stSnapshotsResponse\0225\n\tsnapshots\030\001 \003(\0132\"" +
      ".google.bigtable.admin.v2.Snapshot\022\027\n\017ne",
      "xt_page_token\030\002 \001(\t\"%\n\025DeleteSnapshotReq" +
      "uest\022\014\n\004name\030\001 \001(\t2\217\016\n\022BigtableTableAdmi" +
      "n\022\223\001\n\013CreateTable\022,.google.bigtable.admi" +
      "n.v2.CreateTableRequest\032\037.google.bigtabl" +
      "e.admin.v2.Table\"5\202\323\344\223\002/\"*/v2/{parent=pr" +
      "ojects/*/instances/*}/tables:\001*\022\274\001\n\027Crea" +
      "teTableFromSnapshot\0228.google.bigtable.ad" +
      "min.v2.CreateTableFromSnapshotRequest\032\035." +
      "google.longrunning.Operation\"H\202\323\344\223\002B\"=/v" +
      "2/{parent=projects/*/instances/*}/tables",
      ":createFromSnapshot:\001*\022\233\001\n\nListTables\022+." +
      "google.bigtable.admin.v2.ListTablesReque" +
      "st\032,.google.bigtable.admin.v2.ListTables" +
      "Response\"2\202\323\344\223\002,\022*/v2/{parent=projects/*" +
      "/instances/*}/tables\022\212\001\n\010GetTable\022).goog" +
      "le.bigtable.admin.v2.GetTableRequest\032\037.g" +
      "oogle.bigtable.admin.v2.Table\"2\202\323\344\223\002,\022*/" +
      "v2/{name=projects/*/instances/*/tables/*" +
      "}\022\207\001\n\013DeleteTable\022,.google.bigtable.admi" +
      "n.v2.DeleteTableRequest\032\026.google.protobu",
      "f.Empty\"2\202\323\344\223\002,**/v2/{name=projects/*/in" +
      "stances/*/tables/*}\022\272\001\n\024ModifyColumnFami" +
      "lies\0225.google.bigtable.admin.v2.ModifyCo" +
      "lumnFamiliesRequest\032\037.google.bigtable.ad" +
      "min.v2.Table\"J\202\323\344\223\002D\"?/v2/{name=projects" +
      "/*/instances/*/tables/*}:modifyColumnFam" +
      "ilies:\001*\022\231\001\n\014DropRowRange\022-.google.bigta" +
      "ble.admin.v2.DropRowRangeRequest\032\026.googl" +
      "e.protobuf.Empty\"B\202\323\344\223\002<\"7/v2/{name=proj" +
      "ects/*/instances/*/tables/*}:dropRowRang",
      "e:\001*\022\236\001\n\rSnapshotTable\022..google.bigtable" +
      ".admin.v2.SnapshotTableRequest\032\035.google." +
      "longrunning.Operation\">\202\323\344\223\0028\"3/v2/{name" +
      "=projects/*/instances/*/tables/*}:snapsh" +
      "ot:\001*\022\241\001\n\013GetSnapshot\022,.google.bigtable." +
      "admin.v2.GetSnapshotRequest\032\".google.big" +
      "table.admin.v2.Snapshot\"@\202\323\344\223\002:\0228/v2/{na" +
      "me=projects/*/instances/*/clusters/*/sna" +
      "pshots/*}\022\262\001\n\rListSnapshots\022..google.big" +
      "table.admin.v2.ListSnapshotsRequest\032/.go",
      "ogle.bigtable.admin.v2.ListSnapshotsResp" +
      "onse\"@\202\323\344\223\002:\0228/v2/{parent=projects/*/ins" +
      "tances/*/clusters/*}/snapshots\022\233\001\n\016Delet" +
      "eSnapshot\022/.google.bigtable.admin.v2.Del" +
      "eteSnapshotRequest\032\026.google.protobuf.Emp" +
      "ty\"@\202\323\344\223\002:*8/v2/{name=projects/*/instanc" +
      "es/*/clusters/*/snapshots/*}B\210\001\n\034com.goo" +
      "gle.bigtable.admin.v2B\027BigtableTableAdmi" +
      "nProtoP\001ZMgoogle.golang.org/genproto/goo" +
      "gleapis/bigtable/admin/v2/tableadmin;tab",
      "leadminb\006proto3"
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
          com.google.bigtable.admin.v2.TableProto.getDescriptor(),
          com.google.longrunning.OperationsProto.getDescriptor(),
          com.google.protobuf.DurationProto.getDescriptor(),
          com.google.protobuf.EmptyProto.getDescriptor(),
        }, assigner);
    internal_static_google_bigtable_admin_v2_CreateTableRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_bigtable_admin_v2_CreateTableRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateTableRequest_descriptor,
        new java.lang.String[] { "Parent", "TableId", "Table", "InitialSplits", });
    internal_static_google_bigtable_admin_v2_CreateTableRequest_Split_descriptor =
      internal_static_google_bigtable_admin_v2_CreateTableRequest_descriptor.getNestedTypes().get(0);
    internal_static_google_bigtable_admin_v2_CreateTableRequest_Split_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateTableRequest_Split_descriptor,
        new java.lang.String[] { "Key", });
    internal_static_google_bigtable_admin_v2_CreateTableFromSnapshotRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_bigtable_admin_v2_CreateTableFromSnapshotRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_CreateTableFromSnapshotRequest_descriptor,
        new java.lang.String[] { "Parent", "TableId", "SourceSnapshot", });
    internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_google_bigtable_admin_v2_DropRowRangeRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor,
        new java.lang.String[] { "Name", "RowKeyPrefix", "DeleteAllDataFromTable", "Target", });
    internal_static_google_bigtable_admin_v2_ListTablesRequest_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_google_bigtable_admin_v2_ListTablesRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListTablesRequest_descriptor,
        new java.lang.String[] { "Parent", "View", "PageToken", });
    internal_static_google_bigtable_admin_v2_ListTablesResponse_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_google_bigtable_admin_v2_ListTablesResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListTablesResponse_descriptor,
        new java.lang.String[] { "Tables", "NextPageToken", });
    internal_static_google_bigtable_admin_v2_GetTableRequest_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_google_bigtable_admin_v2_GetTableRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_GetTableRequest_descriptor,
        new java.lang.String[] { "Name", "View", });
    internal_static_google_bigtable_admin_v2_DeleteTableRequest_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_google_bigtable_admin_v2_DeleteTableRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_DeleteTableRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_descriptor,
        new java.lang.String[] { "Name", "Modifications", });
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_Modification_descriptor =
      internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_descriptor.getNestedTypes().get(0);
    internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_Modification_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ModifyColumnFamiliesRequest_Modification_descriptor,
        new java.lang.String[] { "Id", "Create", "Update", "Drop", "Mod", });
    internal_static_google_bigtable_admin_v2_SnapshotTableRequest_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_google_bigtable_admin_v2_SnapshotTableRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_SnapshotTableRequest_descriptor,
        new java.lang.String[] { "Name", "Cluster", "SnapshotId", "Ttl", });
    internal_static_google_bigtable_admin_v2_GetSnapshotRequest_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_google_bigtable_admin_v2_GetSnapshotRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_GetSnapshotRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_bigtable_admin_v2_ListSnapshotsRequest_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_google_bigtable_admin_v2_ListSnapshotsRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListSnapshotsRequest_descriptor,
        new java.lang.String[] { "Parent", "PageSize", "PageToken", });
    internal_static_google_bigtable_admin_v2_ListSnapshotsResponse_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_google_bigtable_admin_v2_ListSnapshotsResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_ListSnapshotsResponse_descriptor,
        new java.lang.String[] { "Snapshots", "NextPageToken", });
    internal_static_google_bigtable_admin_v2_DeleteSnapshotRequest_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_google_bigtable_admin_v2_DeleteSnapshotRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_bigtable_admin_v2_DeleteSnapshotRequest_descriptor,
        new java.lang.String[] { "Name", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.bigtable.admin.v2.TableProto.getDescriptor();
    com.google.longrunning.OperationsProto.getDescriptor();
    com.google.protobuf.DurationProto.getDescriptor();
    com.google.protobuf.EmptyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
