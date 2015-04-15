package com.google.cloud.bigtable.hbase.adapters.filters;

import com.google.bigtable.v1.RowFilter;
import com.google.bigtable.v1.RowFilter.Chain;
import com.google.bigtable.v1.RowFilter.Interleave;

import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a FilterList into either a RowFilter with chaining or interleaving.
 */
public class FilterListAdapter
    implements TypedFilterAdapter<FilterList>, UnsupportedStatusCollector<FilterList> {
  private final FilterAdapter subFilterAdapter;

  public FilterListAdapter(FilterAdapter subFilterAdapter) {
    this.subFilterAdapter = subFilterAdapter;
  }

  @Override
  public RowFilter adapt(FilterAdapterContext context, FilterList filter) throws IOException {
    if (filter.getOperator() == Operator.MUST_PASS_ALL) {
      return filterMustPassAll(context, filter);
    } else {
      return filterMustPassOne(context, filter);
    }
  }

  private RowFilter filterMustPassOne(FilterAdapterContext context, FilterList filter)
      throws IOException {
    Interleave.Builder interLeaveBuilder = Interleave.newBuilder();
    for (Filter subFilter : filter.getFilters()) {
      interLeaveBuilder.addFilters(subFilterAdapter.adaptFilter(context, subFilter));
    }
    return RowFilter.newBuilder()
        .setInterleave(interLeaveBuilder)
        .build();
  }

  private RowFilter filterMustPassAll(FilterAdapterContext context, FilterList filter)
      throws IOException {
    Chain.Builder chainBuilder = Chain.newBuilder();
    for (Filter subFilter : filter.getFilters()) {
      chainBuilder.addFilters(subFilterAdapter.adaptFilter(context, subFilter));
    }
    return RowFilter.newBuilder()
        .setChain(chainBuilder)
        .build();
  }

  @Override
  public FilterSupportStatus isFilterSupported(
      FilterAdapterContext context,
      FilterList filter) {
    List<FilterSupportStatus> unsupportedSubfilters = new ArrayList<>();
    collectUnsupportedStatuses(context, filter, unsupportedSubfilters);
    if (!unsupportedSubfilters.isEmpty()) {
      return FilterSupportStatus.SUPPORTED;
    } else {
      return FilterSupportStatus.newCompositeNotSupported(unsupportedSubfilters);
    }
  }

  @Override
  public void collectUnsupportedStatuses(
      FilterAdapterContext context,
      FilterList filter,
      List<FilterSupportStatus> unsupportedStatuses) {
    for (Filter subFilter : filter.getFilters()) {
      subFilterAdapter.collectUnsupportedStatuses(context, subFilter, unsupportedStatuses);
    }
  }
}
