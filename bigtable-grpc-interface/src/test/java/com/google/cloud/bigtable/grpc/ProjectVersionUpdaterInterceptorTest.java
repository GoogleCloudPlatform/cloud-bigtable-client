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
package com.google.cloud.bigtable.grpc;

import static org.mockito.Mockito.when;

import com.google.bigtable.v1.BigtableServiceGrpc;
import com.google.bigtable.v1.MutateRowRequest;
import com.google.protobuf.Empty;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.grpc.Call;
import io.grpc.Channel;
import io.grpc.Metadata;

@RunWith(JUnit4.class)
public class ProjectVersionUpdaterInterceptorTest {

  @Mock
  private Channel channelStub;
  @Mock
  private Call<MutateRowRequest, Empty> callStub;
  @Mock
  private Call.Listener<Empty> responseListenerStub;

  private ProjectVersionUpdaterInterceptor interceptor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    interceptor = new ProjectVersionUpdaterInterceptor("project_version");
  }

  @Test
  public void interceptCall_newHeader() {
    when(channelStub.newCall(BigtableServiceGrpc.CONFIG.mutateRow)).thenReturn(callStub);

    Call<MutateRowRequest, Empty> wrappedCall =
        interceptor.interceptCall(BigtableServiceGrpc.CONFIG.mutateRow, channelStub);
    Metadata.Headers headers = new Metadata.Headers();
    wrappedCall.start(responseListenerStub, headers);

    Metadata.Key<String> key = Metadata.Key.of("User-Agent", Metadata.ASCII_STRING_MARSHALLER);
    Assert.assertEquals("bigtable-hbase/project_version", headers.get(key));
  }

  @Test
  public void interceptCall_appendHeader() {
    when(channelStub.newCall(BigtableServiceGrpc.CONFIG.mutateRow)).thenReturn(callStub);

    Call<MutateRowRequest, Empty> wrappedCall =
        interceptor.interceptCall(BigtableServiceGrpc.CONFIG.mutateRow, channelStub);
    Metadata.Headers headers = new Metadata.Headers();
    Metadata.Key<String> key = Metadata.Key.of("User-Agent", Metadata.ASCII_STRING_MARSHALLER);
    headers.put(key, "dummy");
    wrappedCall.start(responseListenerStub, headers);

    Assert.assertEquals("dummy bigtable-hbase/project_version", headers.get(key));
  }
}
