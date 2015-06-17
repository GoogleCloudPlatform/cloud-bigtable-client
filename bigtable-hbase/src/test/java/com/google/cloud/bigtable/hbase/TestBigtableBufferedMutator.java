/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.cloud.bigtable.hbase;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.AbstractBigtableConnection;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutator.ExceptionListener;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Row;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.cloud.bigtable.hbase.BigtableBufferedMutator.RequestAccountant;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests for {@link BigtableBufferedMutator}
 */
@RunWith(JUnit4.class)
public class TestBigtableBufferedMutator {

  @Mock
  BatchExecutor executor;

  private BigtableBufferedMutator underTest;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    setup();
  }

  @After
  public void tearDown() throws IOException{
    if (underTest != null) {
      underTest.close();
    }
  }

  private void setup() {
    setup(new BufferedMutator.ExceptionListener() {
      @Override
      public void onException(RetriesExhaustedWithDetailsException exception,
          BufferedMutator mutator) throws RetriesExhaustedWithDetailsException {
        throw exception;
      }
    });
  }

  private void setup(ExceptionListener listener) {
    underTest = new BigtableBufferedMutator(executor,
        AbstractBigtableConnection.BIGTABLE_BUFFERED_MUTATOR_MAX_MEMORY_DEFAULT,
        listener,
        null,
        AbstractBigtableConnection.MAX_INFLIGHT_RPCS_DEFAULT,
        TableName.valueOf("TABLE"));
  }

  @Test
  public void testMutation() throws IOException {
    when(executor.issueRequest(any(Row.class))).thenReturn(mock(ListenableFuture.class));
    Put put = new Put(new byte[1]);
    underTest.mutate(put);
    Mockito.verify(executor, times(1)).issueRequest(any(Row.class));
    Assert.assertEquals(put.heapSize(), underTest.sizeManager.getHeapSize());
    RequestAccountant callback = underTest.sizeManager.activeRequests.values().iterator().next();
    callback.onSuccess(null);
    Assert.assertFalse(underTest.hasInflightRequests());
  }

  @Test
  public void testInvalidPut() throws Exception {
    when(executor.issueRequest(any(Row.class))).thenThrow(new RuntimeException());
    try {
      underTest.mutate(new Put(new byte[0]));
    } catch (RuntimeException ignored) {
      // The RuntimeException is expected behavior
    }
    Assert.assertFalse(underTest.hasInflightRequests());
    Assert.assertEquals(0l, underTest.sizeManager.getHeapSize());
  }

  @Test
  public void testException() {
    underTest.hasExceptions.set(true);
    underTest.globalExceptions.add(
        new BigtableBufferedMutator.MutationException(null, new Exception()));

    try {
      underTest.handleExceptions();
      Assert.fail("expected RetriesExhaustedWithDetailsException");
    } catch (RetriesExhaustedWithDetailsException expected) {
      // Expected
    }
  }
}
