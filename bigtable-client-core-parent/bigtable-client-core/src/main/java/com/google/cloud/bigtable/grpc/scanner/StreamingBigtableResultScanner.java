/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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
package com.google.cloud.bigtable.grpc.scanner;

import java.io.IOException;

import com.google.bigtable.v2.Row;
import com.google.cloud.bigtable.grpc.io.CancellationToken;
import com.google.common.base.Preconditions;

/**
 * A {@link ResultScanner} implementation against the v2 bigtable API.
 */
public class StreamingBigtableResultScanner extends AbstractBigtableResultScanner {

  private final CancellationToken cancellationToken;
  private final ResponseQueueReader<Row> responseQueueReader;

  public StreamingBigtableResultScanner(
      ResponseQueueReader<Row> responseQueueReader,
      CancellationToken cancellationToken) {
    Preconditions.checkArgument(cancellationToken != null, "cancellationToken cannot be null");
    this.cancellationToken = cancellationToken;
    this.responseQueueReader = responseQueueReader;
  }

  @Override
  public Row next() throws IOException {
    return responseQueueReader.getNextMergedRow();
  }

  @Override
  public int available() {
    return responseQueueReader.available();
  }

  @Override
  public void close() throws IOException {
    cancellationToken.cancel();
  }
}
