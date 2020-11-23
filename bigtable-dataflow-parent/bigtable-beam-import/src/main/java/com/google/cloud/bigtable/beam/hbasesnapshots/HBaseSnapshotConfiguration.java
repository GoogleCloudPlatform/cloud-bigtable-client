/*
 * Copyright 2017 Google Inc. All Rights Reserved.
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
package com.google.cloud.bigtable.beam.hbasesnapshots;

import static java.lang.System.*;

import com.google.common.base.Preconditions;
import org.apache.beam.sdk.io.FileBasedSource;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;

/**
 * A {@link FileBasedSource} that can read hadoop's {@link SequenceFile}s.
 *
 * @param <K> The type of the {@link SequenceFile} key.
 * @param <V> The type of the {@link SequenceFile} value.
 */
class HBaseSnapshotConfiguration {

  private static final Log LOG = LogFactory.getLog(HBaseSnapshotConfiguration.class);

  private final Configuration hbaseConf;

  /**
   * Constructs a new top level source.
   *
   * @param snapshotDir The path or pattern of the file(s) to read.
   */
  HBaseSnapshotConfiguration(
      ValueProvider<String> snapshotDir,
      ValueProvider<String> snapshotName,
      ValueProvider<String> restoreDir) {

    Preconditions.checkArgument(
        snapshotDir.toString().startsWith("gs://"),
        "snapshot folder must be hosted in a GCS bucket ");

    Configuration conf = HBaseConfiguration.create();
    try {
      conf.set("hbase.rootdir", snapshotDir.toString());
      // conf.set("hadoop.home.rootdir", "gs://lichng-gcs/");
      conf.set("fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS");
      conf.set("fs.gs.project.id", "google.com:cloud-bigtable-dev");
      conf.set("fs.defaultFS", "gs://lichng-gcs");
      conf.set("google.cloud.auth.service.account.enable", "true");
      // cache native libs from gcs bucket not working!
      // DistributedCache.createSymlink(conf);
      // DistributedCache.addCacheFile(
      //     new URI("gs://lichng-gcs/native/libhadoop.so.1.0.0#hadoop"), conf);
      // DistributedCache.addCacheFile(
      //     new URI("gs://lichng-gcs/native/libsnappy.so.1.3.0#libsnappy.so"), conf);
      //
      // System.loadLibrary("hadoop");
      // System.loadLibrary("libsnappy.so");

      // conf.set(
      //     "io.compression.codecs",
      //
      // "org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.BZip2Codec,org.apache.hadoop.io.compress.DeflateCodec,org.apache.hadoop.io.compress.SnappyCodec,org.apache.hadoop.io.compress.Lz4Codec");
      conf.setClass(
          "mapreduce.job.inputformat.class", TableSnapshotInputFormat.class, InputFormat.class);
      conf.setClass("key.class", ImmutableBytesWritable.class, Writable.class);
      conf.setClass("value.class", Result.class, Object.class);
      ClientProtos.Scan proto = ProtobufUtil.toScan(new Scan().setBatch(1000).setCaching(1000));
      conf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray()));

      this.LOG.debug(conf);
      Job job = Job.getInstance(conf); // creates internal clone of hbaseConf
      TableSnapshotInputFormat.setInput(
          job, snapshotName.toString(), new Path(restoreDir.toString()));
      conf = job.getConfiguration(); // extract the modified clone
    } catch (Exception e) {
      this.LOG.fatal(e);
      exit(-1);
    } finally {
      this.hbaseConf = conf;
      /*for (Map.Entry<String, String> entry : conf) {
        this.LOG.error("DEBUG:" + entry.getKey() + ":" + entry.getValue());
      }*/
    }
  }

  public Configuration getHbaseConf() {
    return hbaseConf;
  }
}