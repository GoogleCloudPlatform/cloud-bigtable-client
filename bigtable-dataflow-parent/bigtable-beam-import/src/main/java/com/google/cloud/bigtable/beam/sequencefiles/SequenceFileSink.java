package com.google.cloud.bigtable.beam.sequencefiles;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.util.MimeTypes;
import org.apache.beam.sdk.values.KV;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.serializer.Serialization;

public class SequenceFileSink<K,V> extends FileBasedSink<KV<K, V>> {
  private final Class<K> keyClass;
  private final Class<V> valueClass;
  private final String[] serializationNames;

  public SequenceFileSink(
      ValueProvider<ResourceId> baseOutputDirectoryProvider,
      FilenamePolicy filenamePolicy,
      Class<K> keyClass, Class<V> valueClass,
      List<Class<? extends Serialization<?>>> serializations) {
    super(baseOutputDirectoryProvider, filenamePolicy, CompressionType.UNCOMPRESSED);

    this.keyClass = keyClass;
    this.valueClass = valueClass;

    serializationNames = new String[serializations.size()];
    for (int i = 0; i < serializations.size(); i++) {
      serializationNames[i] = serializations.get(i).getName();
    }
  }

  @Override
  public WriteOperation<KV<K, V>> createWriteOperation() {
    return new SeqFileWriteOperation<>(this, keyClass, valueClass, serializationNames);
  }

  private static class SeqFileWriteOperation<K,V> extends WriteOperation<KV<K,V>> {
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final String[] serializationNames;

    public SeqFileWriteOperation(FileBasedSink<KV<K, V>> sink, Class<K> keyClass,
        Class<V> valueClass, String[] serializationNames) {
      super(sink);
      this.keyClass = keyClass;
      this.valueClass = valueClass;
      this.serializationNames = serializationNames;
    }

    @Override
    public Writer<KV<K, V>> createWriter() throws Exception {
      return new SeqFileWriter<>(this, keyClass, valueClass, serializationNames);
    }
  }

  private static class SeqFileWriter<K,V> extends Writer<KV<K,V>> {

    private final String[] serializationNames;
    private SequenceFile.Writer sequenceFile;
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public SeqFileWriter(WriteOperation<KV<K, V>> writeOperation, Class<K> keyClass, Class<V> valueClass, String[] serializationNames) {
      super(writeOperation, MimeTypes.BINARY);
      this.keyClass = keyClass;
      this.valueClass = valueClass;
      this.serializationNames = serializationNames;
    }

    @Override
    protected void prepareWrite(WritableByteChannel channel) throws Exception {
      Configuration configuration = new Configuration(false);
      if (serializationNames.length > 0) {
        configuration.setStrings("io.serializations", serializationNames);
      }

      FSDataOutputStream outputStream = new FSDataOutputStream(new OutputStreamWrapper(channel), new Statistics("dataflow"));
      sequenceFile = SequenceFile.createWriter(configuration,
          SequenceFile.Writer.stream(outputStream),
          SequenceFile.Writer.keyClass(keyClass),
          SequenceFile.Writer.valueClass(valueClass),
          SequenceFile.Writer.compression(SequenceFile.CompressionType.BLOCK)
      );

    }

    @Override
    protected void finishWrite() throws Exception {
      sequenceFile.close();
      super.finishWrite();
    }

    @Override
    public void write(KV<K, V> value) throws Exception {
      sequenceFile.append(value.getKey(), value.getValue());
    }
  }


  static class OutputStreamWrapper extends OutputStream {
    private final WritableByteChannel inner;
    private final ByteBuffer singleByteBuffer = ByteBuffer.allocate(1);

    public OutputStreamWrapper(WritableByteChannel inner) {
      this.inner = inner;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      int written = 0;

      ByteBuffer byteBuffer = ByteBuffer.wrap(b, off, len);

      while(written < len) {
        byteBuffer.position(written + off);
        written += this.inner.write(byteBuffer);
      }
    }

    @Override
    public void write(int b) throws IOException {
      singleByteBuffer.clear();
      singleByteBuffer.put((byte)b);

      int written = 0;

      while(written == 0) {
        singleByteBuffer.position(0);
        written = this.inner.write(singleByteBuffer);
      }
    }
  }
}
