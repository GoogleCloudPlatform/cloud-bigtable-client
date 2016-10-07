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
package com.google.cloud.bigtable.grpc.scanner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.cloud.bigtable.grpc.io.CancellationToken;

@RunWith(JUnit4.class)
public class StreamingBigtableResultScannerTest {

  @Mock
  ResponseQueueReader reader;

  @Mock
  CancellationToken cancellationToken;

  StreamingBigtableResultScanner scanner;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    scanner = new StreamingBigtableResultScanner(reader, cancellationToken);
  }

  @Test
  public void cancellationIsSignalled() throws IOException {
    scanner.close();
    verify(cancellationToken, times(1)).cancel();
  }

  @Test
  public void testNext() throws IOException {
    scanner.next();
    verify(reader, times(1)).getNextMergedRow();
    scanner.close();
  }
}
