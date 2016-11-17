// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/bigtable_table_admin.proto

package com.google.bigtable.admin.v2;

/**
 * Protobuf type {@code google.bigtable.admin.v2.DropRowRangeRequest}
 *
 * <pre>
 * Request message for [google.bigtable.admin.v2.BigtableTableAdmin.DropRowRange][google.bigtable.admin.v2.BigtableTableAdmin.DropRowRange]
 * </pre>
 */
public  final class DropRowRangeRequest extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.bigtable.admin.v2.DropRowRangeRequest)
    DropRowRangeRequestOrBuilder {
  // Use DropRowRangeRequest.newBuilder() to construct.
  private DropRowRangeRequest(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private DropRowRangeRequest() {
    name_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private DropRowRangeRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            name_ = s;
            break;
          }
          case 18: {
            targetCase_ = 2;
            target_ = input.readBytes();
            break;
          }
          case 24: {
            targetCase_ = 3;
            target_ = input.readBool();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new RuntimeException(e.setUnfinishedMessage(this));
    } catch (java.io.IOException e) {
      throw new RuntimeException(
          new com.google.protobuf.InvalidProtocolBufferException(
              e.getMessage()).setUnfinishedMessage(this));
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.bigtable.admin.v2.BigtableTableAdminProto.internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.bigtable.admin.v2.BigtableTableAdminProto.internal_static_google_bigtable_admin_v2_DropRowRangeRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.bigtable.admin.v2.DropRowRangeRequest.class, com.google.bigtable.admin.v2.DropRowRangeRequest.Builder.class);
  }

  private int targetCase_ = 0;
  private java.lang.Object target_;
  public enum TargetCase
      implements com.google.protobuf.Internal.EnumLite {
    ROW_KEY_PREFIX(2),
    DELETE_ALL_DATA_FROM_TABLE(3),
    TARGET_NOT_SET(0);
    private int value = 0;
    private TargetCase(int value) {
      this.value = value;
    }
    public static TargetCase valueOf(int value) {
      switch (value) {
        case 2: return ROW_KEY_PREFIX;
        case 3: return DELETE_ALL_DATA_FROM_TABLE;
        case 0: return TARGET_NOT_SET;
        default: throw new java.lang.IllegalArgumentException(
          "Value is undefined for this oneof enum.");
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public TargetCase
  getTargetCase() {
    return TargetCase.valueOf(
        targetCase_);
  }

  public static final int NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object name_;
  /**
   * <code>optional string name = 1;</code>
   *
   * <pre>
   * The unique name of the table on which to drop a range of rows.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
   * </pre>
   */
  public java.lang.String getName() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      name_ = s;
      return s;
    }
  }
  /**
   * <code>optional string name = 1;</code>
   *
   * <pre>
   * The unique name of the table on which to drop a range of rows.
   * Values are of the form
   * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
   * </pre>
   */
  public com.google.protobuf.ByteString
      getNameBytes() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      name_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ROW_KEY_PREFIX_FIELD_NUMBER = 2;
  /**
   * <code>optional bytes row_key_prefix = 2;</code>
   *
   * <pre>
   * Delete all rows that start with this row key prefix. Prefix cannot be
   * zero length.
   * </pre>
   */
  public com.google.protobuf.ByteString getRowKeyPrefix() {
    if (targetCase_ == 2) {
      return (com.google.protobuf.ByteString) target_;
    }
    return com.google.protobuf.ByteString.EMPTY;
  }

  public static final int DELETE_ALL_DATA_FROM_TABLE_FIELD_NUMBER = 3;
  /**
   * <code>optional bool delete_all_data_from_table = 3;</code>
   *
   * <pre>
   * Delete all rows in the table. Setting this to false is a no-op.
   * </pre>
   */
  public boolean getDeleteAllDataFromTable() {
    if (targetCase_ == 3) {
      return (java.lang.Boolean) target_;
    }
    return false;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessage.writeString(output, 1, name_);
    }
    if (targetCase_ == 2) {
      output.writeBytes(
          2, (com.google.protobuf.ByteString)((com.google.protobuf.ByteString) target_));
    }
    if (targetCase_ == 3) {
      output.writeBool(
          3, (boolean)((java.lang.Boolean) target_));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(1, name_);
    }
    if (targetCase_ == 2) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(
            2, (com.google.protobuf.ByteString)((com.google.protobuf.ByteString) target_));
    }
    if (targetCase_ == 3) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(
            3, (boolean)((java.lang.Boolean) target_));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.v2.DropRowRangeRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.bigtable.admin.v2.DropRowRangeRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code google.bigtable.admin.v2.DropRowRangeRequest}
   *
   * <pre>
   * Request message for [google.bigtable.admin.v2.BigtableTableAdmin.DropRowRange][google.bigtable.admin.v2.BigtableTableAdmin.DropRowRange]
   * </pre>
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.bigtable.admin.v2.DropRowRangeRequest)
      com.google.bigtable.admin.v2.DropRowRangeRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.bigtable.admin.v2.BigtableTableAdminProto.internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.bigtable.admin.v2.BigtableTableAdminProto.internal_static_google_bigtable_admin_v2_DropRowRangeRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.bigtable.admin.v2.DropRowRangeRequest.class, com.google.bigtable.admin.v2.DropRowRangeRequest.Builder.class);
    }

    // Construct using com.google.bigtable.admin.v2.DropRowRangeRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      name_ = "";

      targetCase_ = 0;
      target_ = null;
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.bigtable.admin.v2.BigtableTableAdminProto.internal_static_google_bigtable_admin_v2_DropRowRangeRequest_descriptor;
    }

    public com.google.bigtable.admin.v2.DropRowRangeRequest getDefaultInstanceForType() {
      return com.google.bigtable.admin.v2.DropRowRangeRequest.getDefaultInstance();
    }

    public com.google.bigtable.admin.v2.DropRowRangeRequest build() {
      com.google.bigtable.admin.v2.DropRowRangeRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.bigtable.admin.v2.DropRowRangeRequest buildPartial() {
      com.google.bigtable.admin.v2.DropRowRangeRequest result = new com.google.bigtable.admin.v2.DropRowRangeRequest(this);
      result.name_ = name_;
      if (targetCase_ == 2) {
        result.target_ = target_;
      }
      if (targetCase_ == 3) {
        result.target_ = target_;
      }
      result.targetCase_ = targetCase_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.bigtable.admin.v2.DropRowRangeRequest) {
        return mergeFrom((com.google.bigtable.admin.v2.DropRowRangeRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.bigtable.admin.v2.DropRowRangeRequest other) {
      if (other == com.google.bigtable.admin.v2.DropRowRangeRequest.getDefaultInstance()) return this;
      if (!other.getName().isEmpty()) {
        name_ = other.name_;
        onChanged();
      }
      switch (other.getTargetCase()) {
        case ROW_KEY_PREFIX: {
          setRowKeyPrefix(other.getRowKeyPrefix());
          break;
        }
        case DELETE_ALL_DATA_FROM_TABLE: {
          setDeleteAllDataFromTable(other.getDeleteAllDataFromTable());
          break;
        }
        case TARGET_NOT_SET: {
          break;
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.bigtable.admin.v2.DropRowRangeRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.bigtable.admin.v2.DropRowRangeRequest) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int targetCase_ = 0;
    private java.lang.Object target_;
    public TargetCase
        getTargetCase() {
      return TargetCase.valueOf(
          targetCase_);
    }

    public Builder clearTarget() {
      targetCase_ = 0;
      target_ = null;
      onChanged();
      return this;
    }


    private java.lang.Object name_ = "";
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * The unique name of the table on which to drop a range of rows.
     * Values are of the form
     * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
     * </pre>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * The unique name of the table on which to drop a range of rows.
     * Values are of the form
     * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
     * </pre>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * The unique name of the table on which to drop a range of rows.
     * Values are of the form
     * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
     * </pre>
     */
    public Builder setName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      name_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * The unique name of the table on which to drop a range of rows.
     * Values are of the form
     * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
     * </pre>
     */
    public Builder clearName() {
      
      name_ = getDefaultInstance().getName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * The unique name of the table on which to drop a range of rows.
     * Values are of the form
     * `projects/&lt;project&gt;/instances/&lt;instance&gt;/tables/&lt;table&gt;`.
     * </pre>
     */
    public Builder setNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      name_ = value;
      onChanged();
      return this;
    }

    /**
     * <code>optional bytes row_key_prefix = 2;</code>
     *
     * <pre>
     * Delete all rows that start with this row key prefix. Prefix cannot be
     * zero length.
     * </pre>
     */
    public com.google.protobuf.ByteString getRowKeyPrefix() {
      if (targetCase_ == 2) {
        return (com.google.protobuf.ByteString) target_;
      }
      return com.google.protobuf.ByteString.EMPTY;
    }
    /**
     * <code>optional bytes row_key_prefix = 2;</code>
     *
     * <pre>
     * Delete all rows that start with this row key prefix. Prefix cannot be
     * zero length.
     * </pre>
     */
    public Builder setRowKeyPrefix(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  targetCase_ = 2;
      target_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bytes row_key_prefix = 2;</code>
     *
     * <pre>
     * Delete all rows that start with this row key prefix. Prefix cannot be
     * zero length.
     * </pre>
     */
    public Builder clearRowKeyPrefix() {
      if (targetCase_ == 2) {
        targetCase_ = 0;
        target_ = null;
        onChanged();
      }
      return this;
    }

    /**
     * <code>optional bool delete_all_data_from_table = 3;</code>
     *
     * <pre>
     * Delete all rows in the table. Setting this to false is a no-op.
     * </pre>
     */
    public boolean getDeleteAllDataFromTable() {
      if (targetCase_ == 3) {
        return (java.lang.Boolean) target_;
      }
      return false;
    }
    /**
     * <code>optional bool delete_all_data_from_table = 3;</code>
     *
     * <pre>
     * Delete all rows in the table. Setting this to false is a no-op.
     * </pre>
     */
    public Builder setDeleteAllDataFromTable(boolean value) {
      targetCase_ = 3;
      target_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bool delete_all_data_from_table = 3;</code>
     *
     * <pre>
     * Delete all rows in the table. Setting this to false is a no-op.
     * </pre>
     */
    public Builder clearDeleteAllDataFromTable() {
      if (targetCase_ == 3) {
        targetCase_ = 0;
        target_ = null;
        onChanged();
      }
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:google.bigtable.admin.v2.DropRowRangeRequest)
  }

  // @@protoc_insertion_point(class_scope:google.bigtable.admin.v2.DropRowRangeRequest)
  private static final com.google.bigtable.admin.v2.DropRowRangeRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.bigtable.admin.v2.DropRowRangeRequest();
  }

  public static com.google.bigtable.admin.v2.DropRowRangeRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DropRowRangeRequest>
      PARSER = new com.google.protobuf.AbstractParser<DropRowRangeRequest>() {
    public DropRowRangeRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new DropRowRangeRequest(input, extensionRegistry);
      } catch (RuntimeException e) {
        if (e.getCause() instanceof
            com.google.protobuf.InvalidProtocolBufferException) {
          throw (com.google.protobuf.InvalidProtocolBufferException)
              e.getCause();
        }
        throw e;
      }
    }
  };

  public static com.google.protobuf.Parser<DropRowRangeRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DropRowRangeRequest> getParserForType() {
    return PARSER;
  }

  public com.google.bigtable.admin.v2.DropRowRangeRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

