/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.grpc;


/**
 * This class encapsulates a tableName.  A tableName is of the form
 * projects/(projectId)/zones/(zoneId)/clusters/(clusterId)/tables/(tableId).
 *
 * @author sduskis
 * @version $Id: $Id
 */
public class BigtableTableName {
  private final String tableName;

  BigtableTableName(String tableName) {
    this.tableName = tableName;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return tableName;
  }
}
