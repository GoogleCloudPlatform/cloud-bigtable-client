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
import com.google.protobuf.ByteString;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.NavigableSet;

public class ScanAdapter
    implements OperationAdapter<Scan, AnviltopServices.ReadTableRequest.Builder> {

  private final static byte[] NULL_CHARACTER_BYTES = Bytes.toBytes("\\x00");
  public static final String ALL_QUALIFIERS = "\\C*";
  public static final String ALL_FAMILIES = ".*";
  public static final String ALL_VERSIONS = "ALL";
  public static final char INTERLEAVE_CHARACTER = '+';

  /**
   * An OutputStream that performs RE2:QuoteMeta as bytes are written.
   */
  protected static class QuoteMetaOutputStream extends OutputStream {
    protected final OutputStream delegate;

    public QuoteMetaOutputStream(OutputStream delegate) {
      this.delegate = delegate;
    }

    public void writeNullCharacterBytes() throws IOException {
      for (byte b : NULL_CHARACTER_BYTES) {
        delegate.write(b);
      }
    }

    @Override
    public void write(int unquoted) throws IOException {
      if (unquoted == 0) { // Special handling for null chars.
        // Note that this special handling is not strictly required for RE2,
        // but this quoting is required for other regexp libraries such as
        // PCRE.
        // Can't use "\\0" since the next character might be a digit.
        writeNullCharacterBytes();
        return;
      }
      if ((unquoted < 'a' || unquoted > 'z')
          && (unquoted < 'A' || unquoted > 'Z')
          && (unquoted < '0' || unquoted > '9')
          && (unquoted != '_')
          // If this is the part of a UTF8 or Latin1 character, we need
          // to copy this byte without escaping.  Experimentally this is
          // what works correctly with the regexp library.
          && (unquoted >= 0)) {
        delegate.write('\\');
      }
      delegate.write(unquoted);
    }
  }

  /**
   * An OutputStream that performs bigtable reader filter expression language quoting of
   * '@', '{', and '}' by pre-pending a '@' to each.
   */
  protected static class QuoteFilterExpressionStream extends OutputStream {
    protected final OutputStream delegate;

    public QuoteFilterExpressionStream(OutputStream delegate) {
      this.delegate = delegate;
    }

    @Override
    public void write(int unquoted) throws IOException {
      if (unquoted == '@' || unquoted == '{' || unquoted == '}') {
        delegate.write('@');
      }
      delegate.write(unquoted);
    }
  }

  /**
   * Write unquoted to the OutputStream applying both RE2:QuoteMeta and Bigtable reader
   * expression quoting.
   * @param unquoted A byte-array, possibly containing bytes outside of the ASCII
   * @param outputStream A stream to write quoted output to
   */
  static void writeQuotedExpression(byte[] unquoted, OutputStream outputStream)
      throws  IOException {
    QuoteFilterExpressionStream quoteFilterExpressionStream =
        new QuoteFilterExpressionStream(outputStream);
    QuoteMetaOutputStream quoteMetaOutputStream =
        new QuoteMetaOutputStream(quoteFilterExpressionStream);

    quoteMetaOutputStream.write(unquoted);
  }

  /**
   * Write a single stream specification of the form (col(family:qualifier, versions) | ts(x,y))
   * The implementation of more filters will change the resultant form.
   * @param outputStream The stream to write the filter specification to
   * @param family The family byte array
   * @param unquotedQualifier The qualifier byte array, unquoted.
   * @param scan The Scan object from which we can extract filters.
   */
  static void writeScanStream(
      OutputStream outputStream, byte[] family, byte[] unquotedQualifier, Scan scan) {
    try {
      outputStream.write('(');

      if (family == null) {
        family = Bytes.toBytes(ALL_FAMILIES);
      }

      String versionPart =
          scan.getMaxVersions() == Integer.MAX_VALUE ?
              ALL_VERSIONS : Integer.toString(scan.getMaxVersions());

      outputStream.write(Bytes.toBytes("col({"));
      outputStream.write(family);
      outputStream.write(':');
      if (unquotedQualifier == null) {
        outputStream.write(Bytes.toBytes(ALL_QUALIFIERS));
      } else {
        writeQuotedExpression(unquotedQualifier, outputStream);
      }
      outputStream.write('}');
      outputStream.write(',');
      outputStream.write(' ');
      outputStream.write(Bytes.toBytes(versionPart));
      outputStream.write(')');

      if (scan.getTimeRange() != null && !scan.getTimeRange().isAllTime()) {
        // Time ranges in Anviltop are inclusive and HBase uses an open-closed interval. As such,
        // subtract one from the upper bound.
        long upperBound = AnviltopConstants.ANVILTOP_TIMEUNIT.convert(
            scan.getTimeRange().getMax() - 1, AnviltopConstants.HBASE_TIMEUNIT);
        long lowerBound = AnviltopConstants.ANVILTOP_TIMEUNIT.convert(
            scan.getTimeRange().getMin(), AnviltopConstants.HBASE_TIMEUNIT);
        outputStream.write(Bytes.toBytes(String.format(" | ts(%s, %s)", lowerBound, upperBound)));
      }

      outputStream.write(')');
    } catch (IOException ioe) {
      throw Throwables.propagate(ioe);
    }
  }

  /**
   * Given a scan construct an anviltop filter string.
   */
  public static byte[] buildFilterByteString(final Scan scan) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Map<byte[],NavigableSet<byte[]>> familyMap = scan.getFamilyMap();

    boolean writeInterleave = false;
    if (!familyMap.isEmpty()) {
      for (Map.Entry<byte[], NavigableSet<byte[]>> entry : familyMap.entrySet()) {
        if (entry.getValue() == null) {
          if (writeInterleave) {
            outputStream.write(INTERLEAVE_CHARACTER);
          }
          writeInterleave = true;
          writeScanStream(outputStream, entry.getKey(), null, scan);
        } else {
          for (byte[] qualifier : entry.getValue()) {
            if (writeInterleave) {
              outputStream.write(INTERLEAVE_CHARACTER);
            }
            writeInterleave = true;
            writeScanStream(outputStream, entry.getKey(), qualifier, scan);
          }
        }
      }
    } else {
      writeScanStream(outputStream, null, null, scan);
    }

    return outputStream.toByteArray();
  }

  public static void throwIfUnsupportedScan(Scan scan) {
    if (scan.getFilter() != null) {
      // TODO: Translate the following Filters:
      // ColumnPrefixFilter = col(fam:prefix.*, N)
      // KeyOnlyFilter = strip_value()
      // MultiplePrefixFilter = Maybe support with col(foo:bar, N) + col(baz:..., N)
      // Possibly ValueFilter with a CompareOp of EQ and ByteArrayComparable of RegexStringComparator
      throw new UnsupportedOperationException(
          "Filters are not supported.");
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
