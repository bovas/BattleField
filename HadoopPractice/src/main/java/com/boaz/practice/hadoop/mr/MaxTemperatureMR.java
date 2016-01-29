package com.boaz.practice.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MaxTemperatureMR extends Configured implements Tool{

	public static class MaxTemperatureMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		
		private NcdcRecordParser parser = new NcdcRecordParser();
		
		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {			
			parser.parse(value);
			if(parser.isValidTemperature()){
				context.write(new Text(parser.getYear()), new IntWritable(parser.getAirTemperature()));
			}
		}
	}
	
	public static class MaxTemperatureReducer extends 
			Reducer<Text, IntWritable, Text, IntWritable>{
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Reducer<Text, IntWritable, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			int maxValue = Integer.MIN_VALUE;
			for(IntWritable value : values){
				maxValue = Math.max(maxValue,value.get());
			}
			context.write(key, new IntWritable(maxValue));
		}
	}
	
	public static void main(String[] args) throws Exception {
		int status = ToolRunner.run(new MaxTemperatureMR(), args);
		System.exit(status);
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.printf("Usage: %s [generic options] <input> <output>\n",getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
		
		Job mrJob = Job.getInstance(getConf(),"Max Temperature finder");
		mrJob.setJarByClass(getClass());
		
		FileInputFormat.addInputPath(mrJob, new Path(args[0]));
		FileOutputFormat.setOutputPath(mrJob, new Path(args[1]));
		
		mrJob.setMapperClass(MaxTemperatureMapper.class);
		mrJob.setReducerClass(MaxTemperatureReducer.class);
		mrJob.setCombinerClass(MaxTemperatureReducer.class);
		
		mrJob.setOutputKeyClass(Text.class);
		mrJob.setOutputValueClass(IntWritable.class);
				
		return mrJob.waitForCompletion(true) ? 0 : 1;
	}

}
