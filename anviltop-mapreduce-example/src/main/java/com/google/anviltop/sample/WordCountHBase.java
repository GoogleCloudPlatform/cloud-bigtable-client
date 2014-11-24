package com.google.anviltop.sample;

import com.google.anviltop.sample.util.TableOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Writes the same results of WordCount to a new table
 * @author sduskis
 */
public class WordCountHBase {

  public static final byte[] CF = "cf".getBytes();
  public static final byte[] COUNT = "count".getBytes();

  public static class TokenizerMapper extends Mapper<Object, Text, ImmutableBytesWritable, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    @Override
    public void map(Object key, Text value, Context context) throws IOException,
        InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      ImmutableBytesWritable word = new ImmutableBytesWritable();
      while (itr.hasMoreTokens()) {
        word.set(Bytes.toBytes(itr.nextToken()));
        context.write(word, one);
      }
    }
  }

  public static class MyTableReducer extends
      TableReducer<ImmutableBytesWritable, IntWritable, ImmutableBytesWritable> {

    @Override
    public void reduce(ImmutableBytesWritable key, Iterable<IntWritable> values, Context context) throws IOException,
        InterruptedException {
      int sum = sum(values);
      Put put = new Put(key.get());
      put.add(CF, COUNT, Bytes.toBytes(sum));
      context.write(null, put);
    }

    public int sum(Iterable<IntWritable> values) {
      int i = 0;
      for (IntWritable val : values) {
        i += val.get();
      }
      return i;
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    for (String arg : otherArgs) {
      System.out.println(arg);
    }
    if (otherArgs.length < 2) {
      System.err.println("Usage: wordcount-hbase <in> [<in>...] <table-name>");
      System.exit(2);
    }
    
    Job job = Job.getInstance(conf, "word count");

    for (int i = 0; i < otherArgs.length - 1; ++i) {
      FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
    }

    TableName tableName = TableName.valueOf(otherArgs[otherArgs.length - 1]);
    try {
      CreateTable.createTable(tableName, conf);
      System.out.println("Created the table. FTW!");
    } catch (Exception e) {
      System.out.println("Yikes.  Couldn't create the table.");
      e.printStackTrace();
    }

    job.setJarByClass(WordCountHBase.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setMapOutputValueClass(IntWritable.class);

    // Using the long form of this method so that the "false" can be set as the last parameter.  That tells
    // TableMapReduceUtil to not add the .jar depenendencies to the job, which causes problems for some reason.
    TableMapReduceUtil.initTableReducerJob(tableName.getNameAsString(), MyTableReducer.class, job,
      null, null, null, null, false);
 
    job.setOutputFormatClass(TableOutputFormat.class);
    DebugUtil.printConf(conf);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }


}
