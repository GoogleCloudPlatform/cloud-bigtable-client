// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/table/v1/bigtable_table_service_messages.proto

package com.google.bigtable.admin.table.v1;

/**
 * Protobuf type {@code google.bigtable.admin.table.v1.ListTablesResponse}
 */
public  final class ListTablesResponse extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.bigtable.admin.table.v1.ListTablesResponse)
    ListTablesResponseOrBuilder {
  // Use ListTablesResponse.newBuilder() to construct.
  private ListTablesResponse(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private ListTablesResponse() {
    tables_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private ListTablesResponse(
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
            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
              tables_ = new java.util.ArrayList<>();
              mutable_bitField0_ |= 0x00000001;
            }
            tables_.add(input.readMessage(com.google.bigtable.admin.table.v1.Table.parser(), extensionRegistry));
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
      if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
        tables_ = java.util.Collections.unmodifiableList(tables_);
      }
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.bigtable.admin.table.v1.BigtableTableServiceMessagesProto.internal_static_google_bigtable_admin_table_v1_ListTablesResponse_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.bigtable.admin.table.v1.BigtableTableServiceMessagesProto.internal_static_google_bigtable_admin_table_v1_ListTablesResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.bigtable.admin.table.v1.ListTablesResponse.class, com.google.bigtable.admin.table.v1.ListTablesResponse.Builder.class);
  }

  public static final int TABLES_FIELD_NUMBER = 1;
  private java.util.List<com.google.bigtable.admin.table.v1.Table> tables_;
  /**
   * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
   *
   * <pre>
   * The tables present in the requested cluster.
   * At present, only the names of the tables are populated.
   * </pre>
   */
  public java.util.List<com.google.bigtable.admin.table.v1.Table> getTablesList() {
    return tables_;
  }
  /**
   * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
   *
   * <pre>
   * The tables present in the requested cluster.
   * At present, only the names of the tables are populated.
   * </pre>
   */
  public java.util.List<? extends com.google.bigtable.admin.table.v1.TableOrBuilder> 
      getTablesOrBuilderList() {
    return tables_;
  }
  /**
   * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
   *
   * <pre>
   * The tables present in the requested cluster.
   * At present, only the names of the tables are populated.
   * </pre>
   */
  public int getTablesCount() {
    return tables_.size();
  }
  /**
   * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
   *
   * <pre>
   * The tables present in the requested cluster.
   * At present, only the names of the tables are populated.
   * </pre>
   */
  public com.google.bigtable.admin.table.v1.Table getTables(int index) {
    return tables_.get(index);
  }
  /**
   * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
   *
   * <pre>
   * The tables present in the requested cluster.
   * At present, only the names of the tables are populated.
   * </pre>
   */
  public com.google.bigtable.admin.table.v1.TableOrBuilder getTablesOrBuilder(
      int index) {
    return tables_.get(index);
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
    for (int i = 0; i < tables_.size(); i++) {
      output.writeMessage(1, tables_.get(i));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < tables_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, tables_.get(i));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.table.v1.ListTablesResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.bigtable.admin.table.v1.ListTablesResponse prototype) {
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
   * Protobuf type {@code google.bigtable.admin.table.v1.ListTablesResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.bigtable.admin.table.v1.ListTablesResponse)
      com.google.bigtable.admin.table.v1.ListTablesResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.bigtable.admin.table.v1.BigtableTableServiceMessagesProto.internal_static_google_bigtable_admin_table_v1_ListTablesResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.bigtable.admin.table.v1.BigtableTableServiceMessagesProto.internal_static_google_bigtable_admin_table_v1_ListTablesResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.bigtable.admin.table.v1.ListTablesResponse.class, com.google.bigtable.admin.table.v1.ListTablesResponse.Builder.class);
    }

    // Construct using com.google.bigtable.admin.table.v1.ListTablesResponse.newBuilder()
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
        getTablesFieldBuilder();
      }
    }
    public Builder clear() {
      super.clear();
      if (tablesBuilder_ == null) {
        tables_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        tablesBuilder_.clear();
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.bigtable.admin.table.v1.BigtableTableServiceMessagesProto.internal_static_google_bigtable_admin_table_v1_ListTablesResponse_descriptor;
    }

    public com.google.bigtable.admin.table.v1.ListTablesResponse getDefaultInstanceForType() {
      return com.google.bigtable.admin.table.v1.ListTablesResponse.getDefaultInstance();
    }

    public com.google.bigtable.admin.table.v1.ListTablesResponse build() {
      com.google.bigtable.admin.table.v1.ListTablesResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.bigtable.admin.table.v1.ListTablesResponse buildPartial() {
      com.google.bigtable.admin.table.v1.ListTablesResponse result = new com.google.bigtable.admin.table.v1.ListTablesResponse(this);
      int from_bitField0_ = bitField0_;
      if (tablesBuilder_ == null) {
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
          tables_ = java.util.Collections.unmodifiableList(tables_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.tables_ = tables_;
      } else {
        result.tables_ = tablesBuilder_.build();
      }
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.bigtable.admin.table.v1.ListTablesResponse) {
        return mergeFrom((com.google.bigtable.admin.table.v1.ListTablesResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.bigtable.admin.table.v1.ListTablesResponse other) {
      if (other == com.google.bigtable.admin.table.v1.ListTablesResponse.getDefaultInstance()) return this;
      if (tablesBuilder_ == null) {
        if (!other.tables_.isEmpty()) {
          if (tables_.isEmpty()) {
            tables_ = other.tables_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTablesIsMutable();
            tables_.addAll(other.tables_);
          }
          onChanged();
        }
      } else {
        if (!other.tables_.isEmpty()) {
          if (tablesBuilder_.isEmpty()) {
            tablesBuilder_.dispose();
            tablesBuilder_ = null;
            tables_ = other.tables_;
            bitField0_ = (bitField0_ & ~0x00000001);
            tablesBuilder_ = 
              com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                 getTablesFieldBuilder() : null;
          } else {
            tablesBuilder_.addAllMessages(other.tables_);
          }
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
      com.google.bigtable.admin.table.v1.ListTablesResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.bigtable.admin.table.v1.ListTablesResponse) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.util.List<com.google.bigtable.admin.table.v1.Table> tables_ =
      java.util.Collections.emptyList();
    private void ensureTablesIsMutable() {
      if (!((bitField0_ & 0x00000001) == 0x00000001)) {
        tables_ = new java.util.ArrayList<>(tables_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilder<
        com.google.bigtable.admin.table.v1.Table, com.google.bigtable.admin.table.v1.Table.Builder, com.google.bigtable.admin.table.v1.TableOrBuilder> tablesBuilder_;

    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public java.util.List<com.google.bigtable.admin.table.v1.Table> getTablesList() {
      if (tablesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(tables_);
      } else {
        return tablesBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public int getTablesCount() {
      if (tablesBuilder_ == null) {
        return tables_.size();
      } else {
        return tablesBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public com.google.bigtable.admin.table.v1.Table getTables(int index) {
      if (tablesBuilder_ == null) {
        return tables_.get(index);
      } else {
        return tablesBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder setTables(
        int index, com.google.bigtable.admin.table.v1.Table value) {
      if (tablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTablesIsMutable();
        tables_.set(index, value);
        onChanged();
      } else {
        tablesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder setTables(
        int index, com.google.bigtable.admin.table.v1.Table.Builder builderForValue) {
      if (tablesBuilder_ == null) {
        ensureTablesIsMutable();
        tables_.set(index, builderForValue.build());
        onChanged();
      } else {
        tablesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder addTables(com.google.bigtable.admin.table.v1.Table value) {
      if (tablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTablesIsMutable();
        tables_.add(value);
        onChanged();
      } else {
        tablesBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder addTables(
        int index, com.google.bigtable.admin.table.v1.Table value) {
      if (tablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTablesIsMutable();
        tables_.add(index, value);
        onChanged();
      } else {
        tablesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder addTables(
        com.google.bigtable.admin.table.v1.Table.Builder builderForValue) {
      if (tablesBuilder_ == null) {
        ensureTablesIsMutable();
        tables_.add(builderForValue.build());
        onChanged();
      } else {
        tablesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder addTables(
        int index, com.google.bigtable.admin.table.v1.Table.Builder builderForValue) {
      if (tablesBuilder_ == null) {
        ensureTablesIsMutable();
        tables_.add(index, builderForValue.build());
        onChanged();
      } else {
        tablesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder addAllTables(
        java.lang.Iterable<? extends com.google.bigtable.admin.table.v1.Table> values) {
      if (tablesBuilder_ == null) {
        ensureTablesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, tables_);
        onChanged();
      } else {
        tablesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder clearTables() {
      if (tablesBuilder_ == null) {
        tables_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        tablesBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public Builder removeTables(int index) {
      if (tablesBuilder_ == null) {
        ensureTablesIsMutable();
        tables_.remove(index);
        onChanged();
      } else {
        tablesBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public com.google.bigtable.admin.table.v1.Table.Builder getTablesBuilder(
        int index) {
      return getTablesFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public com.google.bigtable.admin.table.v1.TableOrBuilder getTablesOrBuilder(
        int index) {
      if (tablesBuilder_ == null) {
        return tables_.get(index);  } else {
        return tablesBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public java.util.List<? extends com.google.bigtable.admin.table.v1.TableOrBuilder> 
         getTablesOrBuilderList() {
      if (tablesBuilder_ != null) {
        return tablesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(tables_);
      }
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public com.google.bigtable.admin.table.v1.Table.Builder addTablesBuilder() {
      return getTablesFieldBuilder().addBuilder(
          com.google.bigtable.admin.table.v1.Table.getDefaultInstance());
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public com.google.bigtable.admin.table.v1.Table.Builder addTablesBuilder(
        int index) {
      return getTablesFieldBuilder().addBuilder(
          index, com.google.bigtable.admin.table.v1.Table.getDefaultInstance());
    }
    /**
     * <code>repeated .google.bigtable.admin.table.v1.Table tables = 1;</code>
     *
     * <pre>
     * The tables present in the requested cluster.
     * At present, only the names of the tables are populated.
     * </pre>
     */
    public java.util.List<com.google.bigtable.admin.table.v1.Table.Builder> 
         getTablesBuilderList() {
      return getTablesFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilder<
        com.google.bigtable.admin.table.v1.Table, com.google.bigtable.admin.table.v1.Table.Builder, com.google.bigtable.admin.table.v1.TableOrBuilder> 
        getTablesFieldBuilder() {
      if (tablesBuilder_ == null) {
        tablesBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<>(
                tables_,
                ((bitField0_ & 0x00000001) == 0x00000001),
                getParentForChildren(),
                isClean());
        tables_ = null;
      }
      return tablesBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:google.bigtable.admin.table.v1.ListTablesResponse)
  }

  // @@protoc_insertion_point(class_scope:google.bigtable.admin.table.v1.ListTablesResponse)
  private static final com.google.bigtable.admin.table.v1.ListTablesResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.bigtable.admin.table.v1.ListTablesResponse();
  }

  public static com.google.bigtable.admin.table.v1.ListTablesResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ListTablesResponse>
      PARSER = new com.google.protobuf.AbstractParser<ListTablesResponse>() {
    public ListTablesResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new ListTablesResponse(input, extensionRegistry);
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

  public static com.google.protobuf.Parser<ListTablesResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ListTablesResponse> getParserForType() {
    return PARSER;
  }

  public com.google.bigtable.admin.table.v1.ListTablesResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

