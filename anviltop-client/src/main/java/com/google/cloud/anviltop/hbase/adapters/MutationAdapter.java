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

import com.google.bigtable.anviltop.AnviltopData;

import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapt a generic Mutation to an anviltop operation.
 *
 * This class uses instanceof checking to determine an appropriate adaptation to apply.
 */
public class MutationAdapter
    implements OperationAdapter<Mutation, AnviltopData.RowMutation.Builder> {

  static class AdapterInstanceMap {
    private Map<Class<?>, OperationAdapter<?, ?>> unsafeMap =
        new HashMap<Class<?>, OperationAdapter<?, ?>>();

    public <S extends Mutation, U extends OperationAdapter<S, AnviltopData.RowMutation.Builder>>
    Class<S> put(Class<S> key, U adapter) {
      unsafeMap.put(key, adapter);
      return key;
    }

    // The only way to add to the unsafeMap is via put which enforces our type constraints at
    // compile-time. The unchecked cast should be safe.
    @SuppressWarnings("unchecked")
    public <S extends Mutation, U extends OperationAdapter<S, AnviltopData.RowMutation.Builder>>
    U get(Class<? extends S> key) {
      return (U) unsafeMap.get(key);
    }
  }

  private final AdapterInstanceMap adapterMap = new AdapterInstanceMap();

  public MutationAdapter(
      OperationAdapter<Delete, AnviltopData.RowMutation.Builder> deleteAdapter,
      OperationAdapter<Put, AnviltopData.RowMutation.Builder> putAdapter,
      OperationAdapter<Increment, AnviltopData.RowMutation.Builder> incrementAdapter,
      OperationAdapter<Append, AnviltopData.RowMutation.Builder> appendAdapter) {
    adapterMap.put(Delete.class, deleteAdapter);
    adapterMap.put(Put.class, putAdapter);
    adapterMap.put(Increment.class, incrementAdapter);
    adapterMap.put(Append.class, appendAdapter);
  }

  @Override
  public AnviltopData.RowMutation.Builder adapt(Mutation mutation) {
    OperationAdapter<Mutation, AnviltopData.RowMutation.Builder> adapter =
        adapterMap.get(mutation.getClass());
    if (adapter == null) {
      throw new UnsupportedOperationException(
          String.format(
              "Cannot adapt mutation of type %s.", mutation.getClass().getCanonicalName()));
    }
    return adapter.adapt(mutation);
  }
}
