// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/admin/v2/instance.proto

package com.google.bigtable.admin.v2;

/**
 * Protobuf type {@code google.bigtable.admin.v2.Instance}
 *
 * <pre>
 * A collection of Bigtable [Tables][google.bigtable.admin.v2.Table] and
 * the resources that serve them.
 * All tables in an instance are served from a single
 * [Cluster][google.bigtable.admin.v2.Cluster].
 * </pre>
 */
public  final class Instance extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.bigtable.admin.v2.Instance)
    InstanceOrBuilder {
  // Use Instance.newBuilder() to construct.
  private Instance(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private Instance() {
    name_ = "";
    displayName_ = "";
    state_ = 0;
    type_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private Instance(
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
            java.lang.String s = input.readStringRequireUtf8();

            displayName_ = s;
            break;
          }
          case 24: {
            int rawValue = input.readEnum();

            state_ = rawValue;
            break;
          }
          case 32: {
            int rawValue = input.readEnum();

            type_ = rawValue;
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
    return com.google.bigtable.admin.v2.InstanceProto.internal_static_google_bigtable_admin_v2_Instance_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.bigtable.admin.v2.InstanceProto.internal_static_google_bigtable_admin_v2_Instance_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.bigtable.admin.v2.Instance.class, com.google.bigtable.admin.v2.Instance.Builder.class);
  }

  /**
   * Protobuf enum {@code google.bigtable.admin.v2.Instance.State}
   *
   * <pre>
   * Possible states of an instance.
   * </pre>
   */
  public enum State
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>STATE_NOT_KNOWN = 0;</code>
     *
     * <pre>
     * The state of the instance could not be determined.
     * </pre>
     */
    STATE_NOT_KNOWN(0, 0),
    /**
     * <code>READY = 1;</code>
     *
     * <pre>
     * The instance has been successfully created and can serve requests
     * to its tables.
     * </pre>
     */
    READY(1, 1),
    /**
     * <code>CREATING = 2;</code>
     *
     * <pre>
     * The instance is currently being created, and may be destroyed
     * if the creation process encounters an error.
     * </pre>
     */
    CREATING(2, 2),
    UNRECOGNIZED(-1, -1),
    ;

    /**
     * <code>STATE_NOT_KNOWN = 0;</code>
     *
     * <pre>
     * The state of the instance could not be determined.
     * </pre>
     */
    public static final int STATE_NOT_KNOWN_VALUE = 0;
    /**
     * <code>READY = 1;</code>
     *
     * <pre>
     * The instance has been successfully created and can serve requests
     * to its tables.
     * </pre>
     */
    public static final int READY_VALUE = 1;
    /**
     * <code>CREATING = 2;</code>
     *
     * <pre>
     * The instance is currently being created, and may be destroyed
     * if the creation process encounters an error.
     * </pre>
     */
    public static final int CREATING_VALUE = 2;


    public final int getNumber() {
      if (index == -1) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    public static State valueOf(int value) {
      switch (value) {
        case 0: return STATE_NOT_KNOWN;
        case 1: return READY;
        case 2: return CREATING;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<State>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        State> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<State>() {
            public State findValueByNumber(int number) {
              return State.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.google.bigtable.admin.v2.Instance.getDescriptor().getEnumTypes().get(0);
    }

    private static final State[] VALUES = values();

    public static State valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private State(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:google.bigtable.admin.v2.Instance.State)
  }

  /**
   * Protobuf enum {@code google.bigtable.admin.v2.Instance.Type}
   *
   * <pre>
   * The type of the instance.
   * </pre>
   */
  public enum Type
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>TYPE_UNSPECIFIED = 0;</code>
     *
     * <pre>
     * The type of the instance is unspecified. If set when creating an
     * instance, a `PRODUCTION` instance will be created. If set when updating
     * an instance, the type will be left unchanged.
     * </pre>
     */
    TYPE_UNSPECIFIED(0, 0),
    /**
     * <code>PRODUCTION = 1;</code>
     *
     * <pre>
     * An instance meant for production use. `serve_nodes` must be set
     * on the cluster.
     * </pre>
     */
    PRODUCTION(1, 1),
    /**
     * <code>DEVELOPMENT = 2;</code>
     *
     * <pre>
     * The instance is meant for development purposes only.
     * It uses shared resources and has no performance or uptime guarantees.
     * After a development instance is created, it can be upgraded by
     * updating the instance to type `PRODUCTION`. An instance created
     * as a production instance cannot be changed to a development instance.
     * When creating a development instance, `serve_nodes` on the cluster must
     * not be set.
     * </pre>
     */
    DEVELOPMENT(2, 2),
    UNRECOGNIZED(-1, -1),
    ;

    /**
     * <code>TYPE_UNSPECIFIED = 0;</code>
     *
     * <pre>
     * The type of the instance is unspecified. If set when creating an
     * instance, a `PRODUCTION` instance will be created. If set when updating
     * an instance, the type will be left unchanged.
     * </pre>
     */
    public static final int TYPE_UNSPECIFIED_VALUE = 0;
    /**
     * <code>PRODUCTION = 1;</code>
     *
     * <pre>
     * An instance meant for production use. `serve_nodes` must be set
     * on the cluster.
     * </pre>
     */
    public static final int PRODUCTION_VALUE = 1;
    /**
     * <code>DEVELOPMENT = 2;</code>
     *
     * <pre>
     * The instance is meant for development purposes only.
     * It uses shared resources and has no performance or uptime guarantees.
     * After a development instance is created, it can be upgraded by
     * updating the instance to type `PRODUCTION`. An instance created
     * as a production instance cannot be changed to a development instance.
     * When creating a development instance, `serve_nodes` on the cluster must
     * not be set.
     * </pre>
     */
    public static final int DEVELOPMENT_VALUE = 2;


    public final int getNumber() {
      if (index == -1) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    public static Type valueOf(int value) {
      switch (value) {
        case 0: return TYPE_UNSPECIFIED;
        case 1: return PRODUCTION;
        case 2: return DEVELOPMENT;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Type>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Type> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Type>() {
            public Type findValueByNumber(int number) {
              return Type.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.google.bigtable.admin.v2.Instance.getDescriptor().getEnumTypes().get(1);
    }

    private static final Type[] VALUES = values();

    public static Type valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private Type(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:google.bigtable.admin.v2.Instance.Type)
  }

  public static final int NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object name_;
  /**
   * <code>optional string name = 1;</code>
   *
   * <pre>
   * (`OutputOnly`)
   * The unique name of the instance. Values are of the form
   * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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
   * (`OutputOnly`)
   * The unique name of the instance. Values are of the form
   * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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

  public static final int DISPLAY_NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object displayName_;
  /**
   * <code>optional string display_name = 2;</code>
   *
   * <pre>
   * The descriptive name for this instance as it appears in UIs.
   * Can be changed at any time, but should be kept globally unique
   * to avoid confusion.
   * </pre>
   */
  public java.lang.String getDisplayName() {
    java.lang.Object ref = displayName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      displayName_ = s;
      return s;
    }
  }
  /**
   * <code>optional string display_name = 2;</code>
   *
   * <pre>
   * The descriptive name for this instance as it appears in UIs.
   * Can be changed at any time, but should be kept globally unique
   * to avoid confusion.
   * </pre>
   */
  public com.google.protobuf.ByteString
      getDisplayNameBytes() {
    java.lang.Object ref = displayName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      displayName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int STATE_FIELD_NUMBER = 3;
  private int state_;
  /**
   * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
   *
   * <pre>
   * (`OutputOnly`)
   * The current state of the instance.
   * </pre>
   */
  public int getStateValue() {
    return state_;
  }
  /**
   * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
   *
   * <pre>
   * (`OutputOnly`)
   * The current state of the instance.
   * </pre>
   */
  public com.google.bigtable.admin.v2.Instance.State getState() {
    com.google.bigtable.admin.v2.Instance.State result = com.google.bigtable.admin.v2.Instance.State.valueOf(state_);
    return result == null ? com.google.bigtable.admin.v2.Instance.State.UNRECOGNIZED : result;
  }

  public static final int TYPE_FIELD_NUMBER = 4;
  private int type_;
  /**
   * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
   *
   * <pre>
   * The type of the instance. Defaults to `PRODUCTION`.
   * </pre>
   */
  public int getTypeValue() {
    return type_;
  }
  /**
   * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
   *
   * <pre>
   * The type of the instance. Defaults to `PRODUCTION`.
   * </pre>
   */
  public com.google.bigtable.admin.v2.Instance.Type getType() {
    com.google.bigtable.admin.v2.Instance.Type result = com.google.bigtable.admin.v2.Instance.Type.valueOf(type_);
    return result == null ? com.google.bigtable.admin.v2.Instance.Type.UNRECOGNIZED : result;
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
    if (!getDisplayNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessage.writeString(output, 2, displayName_);
    }
    if (state_ != com.google.bigtable.admin.v2.Instance.State.STATE_NOT_KNOWN.getNumber()) {
      output.writeEnum(3, state_);
    }
    if (type_ != com.google.bigtable.admin.v2.Instance.Type.TYPE_UNSPECIFIED.getNumber()) {
      output.writeEnum(4, type_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(1, name_);
    }
    if (!getDisplayNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(2, displayName_);
    }
    if (state_ != com.google.bigtable.admin.v2.Instance.State.STATE_NOT_KNOWN.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(3, state_);
    }
    if (type_ != com.google.bigtable.admin.v2.Instance.Type.TYPE_UNSPECIFIED.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(4, type_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.Instance parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.bigtable.admin.v2.Instance parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.admin.v2.Instance parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.bigtable.admin.v2.Instance prototype) {
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
   * Protobuf type {@code google.bigtable.admin.v2.Instance}
   *
   * <pre>
   * A collection of Bigtable [Tables][google.bigtable.admin.v2.Table] and
   * the resources that serve them.
   * All tables in an instance are served from a single
   * [Cluster][google.bigtable.admin.v2.Cluster].
   * </pre>
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.bigtable.admin.v2.Instance)
      com.google.bigtable.admin.v2.InstanceOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.bigtable.admin.v2.InstanceProto.internal_static_google_bigtable_admin_v2_Instance_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.bigtable.admin.v2.InstanceProto.internal_static_google_bigtable_admin_v2_Instance_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.bigtable.admin.v2.Instance.class, com.google.bigtable.admin.v2.Instance.Builder.class);
    }

    // Construct using com.google.bigtable.admin.v2.Instance.newBuilder()
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

      displayName_ = "";

      state_ = 0;

      type_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.bigtable.admin.v2.InstanceProto.internal_static_google_bigtable_admin_v2_Instance_descriptor;
    }

    public com.google.bigtable.admin.v2.Instance getDefaultInstanceForType() {
      return com.google.bigtable.admin.v2.Instance.getDefaultInstance();
    }

    public com.google.bigtable.admin.v2.Instance build() {
      com.google.bigtable.admin.v2.Instance result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.bigtable.admin.v2.Instance buildPartial() {
      com.google.bigtable.admin.v2.Instance result = new com.google.bigtable.admin.v2.Instance(this);
      result.name_ = name_;
      result.displayName_ = displayName_;
      result.state_ = state_;
      result.type_ = type_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.bigtable.admin.v2.Instance) {
        return mergeFrom((com.google.bigtable.admin.v2.Instance)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.bigtable.admin.v2.Instance other) {
      if (other == com.google.bigtable.admin.v2.Instance.getDefaultInstance()) return this;
      if (!other.getName().isEmpty()) {
        name_ = other.name_;
        onChanged();
      }
      if (!other.getDisplayName().isEmpty()) {
        displayName_ = other.displayName_;
        onChanged();
      }
      if (other.state_ != 0) {
        setStateValue(other.getStateValue());
      }
      if (other.type_ != 0) {
        setTypeValue(other.getTypeValue());
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
      com.google.bigtable.admin.v2.Instance parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.bigtable.admin.v2.Instance) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object name_ = "";
    /**
     * <code>optional string name = 1;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The unique name of the instance. Values are of the form
     * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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
     * (`OutputOnly`)
     * The unique name of the instance. Values are of the form
     * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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
     * (`OutputOnly`)
     * The unique name of the instance. Values are of the form
     * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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
     * (`OutputOnly`)
     * The unique name of the instance. Values are of the form
     * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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
     * (`OutputOnly`)
     * The unique name of the instance. Values are of the form
     * `projects/&lt;project&gt;/instances/[a-z][a-z0-9&#92;&#92;-]+[a-z0-9]`.
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

    private java.lang.Object displayName_ = "";
    /**
     * <code>optional string display_name = 2;</code>
     *
     * <pre>
     * The descriptive name for this instance as it appears in UIs.
     * Can be changed at any time, but should be kept globally unique
     * to avoid confusion.
     * </pre>
     */
    public java.lang.String getDisplayName() {
      java.lang.Object ref = displayName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        displayName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string display_name = 2;</code>
     *
     * <pre>
     * The descriptive name for this instance as it appears in UIs.
     * Can be changed at any time, but should be kept globally unique
     * to avoid confusion.
     * </pre>
     */
    public com.google.protobuf.ByteString
        getDisplayNameBytes() {
      java.lang.Object ref = displayName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        displayName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string display_name = 2;</code>
     *
     * <pre>
     * The descriptive name for this instance as it appears in UIs.
     * Can be changed at any time, but should be kept globally unique
     * to avoid confusion.
     * </pre>
     */
    public Builder setDisplayName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      displayName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string display_name = 2;</code>
     *
     * <pre>
     * The descriptive name for this instance as it appears in UIs.
     * Can be changed at any time, but should be kept globally unique
     * to avoid confusion.
     * </pre>
     */
    public Builder clearDisplayName() {
      
      displayName_ = getDefaultInstance().getDisplayName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string display_name = 2;</code>
     *
     * <pre>
     * The descriptive name for this instance as it appears in UIs.
     * Can be changed at any time, but should be kept globally unique
     * to avoid confusion.
     * </pre>
     */
    public Builder setDisplayNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      displayName_ = value;
      onChanged();
      return this;
    }

    private int state_ = 0;
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The current state of the instance.
     * </pre>
     */
    public int getStateValue() {
      return state_;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The current state of the instance.
     * </pre>
     */
    public Builder setStateValue(int value) {
      state_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The current state of the instance.
     * </pre>
     */
    public com.google.bigtable.admin.v2.Instance.State getState() {
      com.google.bigtable.admin.v2.Instance.State result = com.google.bigtable.admin.v2.Instance.State.valueOf(state_);
      return result == null ? com.google.bigtable.admin.v2.Instance.State.UNRECOGNIZED : result;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The current state of the instance.
     * </pre>
     */
    public Builder setState(com.google.bigtable.admin.v2.Instance.State value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      state_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.State state = 3;</code>
     *
     * <pre>
     * (`OutputOnly`)
     * The current state of the instance.
     * </pre>
     */
    public Builder clearState() {
      
      state_ = 0;
      onChanged();
      return this;
    }

    private int type_ = 0;
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
     *
     * <pre>
     * The type of the instance. Defaults to `PRODUCTION`.
     * </pre>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
     *
     * <pre>
     * The type of the instance. Defaults to `PRODUCTION`.
     * </pre>
     */
    public Builder setTypeValue(int value) {
      type_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
     *
     * <pre>
     * The type of the instance. Defaults to `PRODUCTION`.
     * </pre>
     */
    public com.google.bigtable.admin.v2.Instance.Type getType() {
      com.google.bigtable.admin.v2.Instance.Type result = com.google.bigtable.admin.v2.Instance.Type.valueOf(type_);
      return result == null ? com.google.bigtable.admin.v2.Instance.Type.UNRECOGNIZED : result;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
     *
     * <pre>
     * The type of the instance. Defaults to `PRODUCTION`.
     * </pre>
     */
    public Builder setType(com.google.bigtable.admin.v2.Instance.Type value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      type_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>optional .google.bigtable.admin.v2.Instance.Type type = 4;</code>
     *
     * <pre>
     * The type of the instance. Defaults to `PRODUCTION`.
     * </pre>
     */
    public Builder clearType() {
      
      type_ = 0;
      onChanged();
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


    // @@protoc_insertion_point(builder_scope:google.bigtable.admin.v2.Instance)
  }

  // @@protoc_insertion_point(class_scope:google.bigtable.admin.v2.Instance)
  private static final com.google.bigtable.admin.v2.Instance DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.bigtable.admin.v2.Instance();
  }

  public static com.google.bigtable.admin.v2.Instance getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Instance>
      PARSER = new com.google.protobuf.AbstractParser<Instance>() {
    public Instance parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new Instance(input, extensionRegistry);
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

  public static com.google.protobuf.Parser<Instance> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Instance> getParserForType() {
    return PARSER;
  }

  public com.google.bigtable.admin.v2.Instance getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

