package com.boaz.practice.hadoop.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ExportFromHBase {

	
	public static class MyMapper extends TableMapper<Text, Text>  {
		public static final byte[] CF = "Personal".getBytes();
		public static final byte[] ATTR1 = "Name".getBytes();

		private final IntWritable ONE = new IntWritable(1);
	   	private Text text = new Text();
	   	private Text text1= new Text();

	   	public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
	   		if (value.getValue(CF, ATTR1) != null){
	        	String val = new String(value.getValue(CF, ATTR1));
	          	text.set(val);     // we can only emit Writables...

	        	context.write(new Text(Bytes.toString(row.get())),text);
	   	}
	   	}
	}
	
	
	public static class MyReducer extends Reducer<Text, Text, Text, Text>  {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int i = 0;
			for (Text val : values) {
				context.write(key, val);
			}
		}
	}
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		
		Configuration config = HBaseConfiguration.create();
		config.set("fs.default.name", "hdfs://localhost:50000");
		config.set("mapred.job.tracker", "localhost:50001");

		Job job = new Job(config,"HBase To HBase");
		job.setJarByClass(ExportFromHBase.class);     // class that contains mapper and reducer

		Scan scan = new Scan();
		scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
		scan.setCacheBlocks(false);  // don't set to true for MR jobs
		// set other scan attrs

		String sourceTable = "Test";
		
		TableMapReduceUtil.initTableMapperJob(
			sourceTable,        // input table
			scan,               // Scan instance to control CF and attribute selection
			com.boaz.practice.hadoop.hbase.ExportFromHBase.MyMapper.class,     // mapper class
			Text.class,         // mapper output key
			Text.class,  // mapper output value
			job);
		job.setReducerClass(com.boaz.practice.hadoop.hbase.ExportFromHBase.MyReducer.class);    // reducer class
		job.setNumReduceTasks(1);    // at least one, adjust as required
		FileOutputFormat.setOutputPath(job, new Path("/ExportFromHBase-T"));  // adjust directories as required

		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error with job!");
		}
		   
	}

}
