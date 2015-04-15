package com.google.cloud.bigtable.hbase.adapters.filters;

import com.google.bigtable.v1.ColumnRange;
import com.google.bigtable.v1.RowFilter;
import com.google.protobuf.ByteString;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Adapter for a single ColumnRangeFilter to a Cloud Bigtable RowFilter.
 */
public class ColumnRangeFilterAdapter implements TypedFilterAdapter<ColumnRangeFilter> {

  private static final String REQUIRE_SINGLE_FAMILY_MESSAGE =
      "Scan or Get operations using ColumnRangeFilter must "
          + "have a single family specified with #addFamily().";
  private static final FilterSupportStatus UNSUPPORTED_STATUS =
      FilterSupportStatus.newNotSupported(REQUIRE_SINGLE_FAMILY_MESSAGE);

  @Override
  public RowFilter adapt(FilterAdapterContext context, ColumnRangeFilter filter)
      throws IOException {
    byte[] familyName = getSingleFamily(context.getScan());
    ColumnRange.Builder rangeBuilder = ColumnRange.newBuilder();
    rangeBuilder.setFamilyName(Bytes.toString(familyName));

    ByteString startQualifier = ByteString.copyFrom(filter.getMinColumn());
    if (filter.getMinColumnInclusive()) {
      rangeBuilder.setStartQualifierInclusive(startQualifier);
    } else {
      rangeBuilder.setStartQualifierExclusive(startQualifier);
    }

    ByteString endQualifier = ByteString.copyFrom(filter.getMaxColumn());
    if (filter.getMaxColumnInclusive()) {
      rangeBuilder.setEndQualifierInclusive(endQualifier);
    } else {
      rangeBuilder.setEndQualifierExclusive(endQualifier);
    }
    return RowFilter.newBuilder()
        .setColumnRangeFilter(rangeBuilder)
        .build();
  }

  @Override
  public FilterSupportStatus isFilterSupported(
      FilterAdapterContext context,
      ColumnRangeFilter filter) {
    // We require a single column family to be specified:
    int familyCount = context.getScan().numFamilies();
    if (familyCount != 1) {
      return UNSUPPORTED_STATUS;
    }
    return FilterSupportStatus.SUPPORTED;
  }

  byte[] getSingleFamily(Scan scan) {
    return scan.getFamilies()[0];
  }
}
