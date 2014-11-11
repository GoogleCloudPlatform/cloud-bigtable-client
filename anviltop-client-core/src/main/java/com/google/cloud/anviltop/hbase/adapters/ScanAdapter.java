/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.anviltop.hbase.adapters;


import com.google.api.client.util.Throwables;
import com.google.bigtable.anviltop.AnviltopData;
import com.google.bigtable.anviltop.AnviltopServices;
import com.google.cloud.anviltop.hbase.AnviltopConstants;
import com.google.cloud.hadoop.hbase.repackaged.protobuf.ByteString;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import javax.annotation.Nullable;

public class ScanAdapter
    implements OperationAdapter<Scan, AnviltopServices.ReadTableRequest.Builder> {

  /**
   * Simple AutoClosable that makes matching open-close filter elements slightly less error-prone
   * by writing the opening part to the stream on construction and the closing part when exiting
   * a try-block.
   */
  public static class ReaderExpressionScope implements AutoCloseable {

    private final OutputStream stream;
    private final byte[] trailer;

    public ReaderExpressionScope(OutputStream stream, char prefix, char trailer) {
      // This seems rather complex for the task at hand...
      this(stream,
          StandardCharsets.UTF_8.encode(CharBuffer.wrap(new char[]{prefix})).array(),
          StandardCharsets.UTF_8.encode(CharBuffer.wrap(new char[]{trailer})).array());
    }

    public ReaderExpressionScope(OutputStream stream, String prefix, String trailer) {
      this(stream, Bytes.toBytes(prefix), Bytes.toBytes(trailer));
    }

    public ReaderExpressionScope(OutputStream stream, byte[] prefix, byte[] trailer) {
      this.stream = stream;
      this.trailer = trailer;

      try {
        stream.write(prefix);
      } catch (IOException e) {
        throw Throwables.propagate(e);
      }
    }

    @Override
    public void close() throws IOException {
      stream.write(trailer);
    }
  }

  /**
   * Write a single stream specification of the form (col(family:qualifier, versions) | ts(x,y))
   * The implementation of more filters will change the resultant form.
   * @param outputStream The stream to write the filter specification to
   * @param family The family byte array
   * @param unquotedQualifier The qualifier byte array, unquoted.
   * @param maxVersions The maximum number of versions to emit for the cell.
   *  Or Integer.MAX_VALUE for all versions.
   */
  void writeScanStream(
      OutputStream outputStream,
      byte[] family,
      byte[] unquotedQualifier,
      int maxVersions) {
    try {
      if (family == null) {
        family = Bytes.toBytes(ReaderExpressionHelper.ALL_FAMILIES);
      }

      String versionPart =
          maxVersions == Integer.MAX_VALUE ?
              ReaderExpressionHelper.ALL_VERSIONS : Integer.toString(maxVersions);
      try (ReaderExpressionScope scope = new ReaderExpressionScope(outputStream, "(col(", "))")) {
        outputStream.write('{');
        outputStream.write(family);
        outputStream.write(':');
        if (unquotedQualifier == null) {
          outputStream.write(Bytes.toBytes(ReaderExpressionHelper.ALL_QUALIFIERS));
        } else {
          readerExpressionHelper.writeQuotedExpression(unquotedQualifier, outputStream);
        }
        outputStream.write(Bytes.toBytes("}, "));
        outputStream.write(Bytes.toBytes(versionPart));
      }
    } catch (Exception ioe) {
      throw Throwables.propagate(ioe);
    }
  }

  /**
   * Write an adapted Filter to the given OutputStream.
   */
  public void writeFilterStream(OutputStream stream, Filter filter) {
    try {
      stream.write(Bytes.toBytes(" | "));
      filterAdapter.adaptFilterTo(filter, stream);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Given a scan construct an anviltop filter string.
   *
   */
  public byte[] buildFilterByteString(Scan scan) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      Map<byte[],NavigableSet<byte[]>> familyMap = scan.getFamilyMap();
      boolean writeInterleave = false;
      try (ReaderExpressionScope scope = new ReaderExpressionScope(outputStream, '(', ')')) {
        if (!familyMap.isEmpty()) {
          for (Map.Entry<byte[], NavigableSet<byte[]>> entry : familyMap.entrySet()) {
            if (entry.getValue() == null) {
              if (writeInterleave) {
                outputStream.write(ReaderExpressionHelper.INTERLEAVE_CHARACTERS);
              }
              writeInterleave = true;
              writeScanStream(outputStream, entry.getKey(), null, scan.getMaxVersions());
            } else {
              for (byte[] qualifier : entry.getValue()) {
                if (writeInterleave) {
                  outputStream.write(ReaderExpressionHelper.INTERLEAVE_CHARACTERS);
                }
                writeInterleave = true;
                writeScanStream(outputStream, entry.getKey(), qualifier, scan.getMaxVersions());
              }
            }
          }
        } else {
          writeScanStream(outputStream, null, null, scan.getMaxVersions());
        }
      }

      if (scan.getTimeRange() != null && !scan.getTimeRange().isAllTime()) {
        // Time ranges in Anviltop are inclusive and HBase uses an open-closed interval. As such,
        // subtract one from the upper bound.
        long upperBound = AnviltopConstants.ANVILTOP_TIMEUNIT.convert(
            scan.getTimeRange().getMax() - 1, AnviltopConstants.HBASE_TIMEUNIT);
        long lowerBound = AnviltopConstants.ANVILTOP_TIMEUNIT.convert(
            scan.getTimeRange().getMin(), AnviltopConstants.HBASE_TIMEUNIT);
        outputStream.write(Bytes.toBytes(String.format(" | ts(%s, %s)", lowerBound, upperBound)));
      }

      if (scan.getFilter() != null) {
        writeFilterStream(outputStream, scan.getFilter());
      }

      return outputStream.toByteArray();
    } catch (IOException ioException) {
      throw Throwables.propagate(ioException);
    }
  }

  protected final FilterAdapter filterAdapter;
  protected final ReaderExpressionHelper readerExpressionHelper;

  public ScanAdapter(FilterAdapter filterAdapter) {
    this.filterAdapter = filterAdapter;
    this.readerExpressionHelper = new ReaderExpressionHelper();
  }

  public void throwIfUnsupportedScan(Scan scan) {
    if (scan.getFilter() != null) {
      filterAdapter.throwIfUnsupportedFilter(scan.getFilter());
    }

    if (scan.getMaxResultsPerColumnFamily() != -1) {
      throw new UnsupportedOperationException(
          "Limiting of max results per column family is not supported.");
    }
  }

  @Override
  public AnviltopServices.ReadTableRequest.Builder adapt(Scan operation) {
    throwIfUnsupportedScan(operation);
    AnviltopServices.ReadTableRequest.Builder result =
        AnviltopServices.ReadTableRequest.newBuilder();

    AnviltopData.ReadOptions.Builder optionsBuilder = AnviltopData.ReadOptions.newBuilder();

    byte[] filter = buildFilterByteString(operation);
    optionsBuilder.setFilterBytes(ByteString.copyFrom(filter));
    optionsBuilder.addRangesBuilder()
        .setStart(ByteString.copyFrom(operation.getStartRow()))
        .setEnd(ByteString.copyFrom(operation.getStopRow()));
    result.setOptions(optionsBuilder);
    return result;
  }
}
