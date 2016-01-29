package com.boaz.practice.hadoop.mr;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
//import org.samples.mapreduce.training.counters.USERCOUNTER;

public class DistributedCache {
	/*public enum COUNTER{
		MANAGER
	}*/

    public static class MapEmployee extends
            Mapper<LongWritable, Text, Text, IntWritable> {

        // setup , map, run, cleanup
    	private String DesignationName;
    	private Text outKey = new Text();
    	private IntWritable outValue = new IntWritable();
    	
    	public void setup(Context context) throws IOException,InterruptedException{
    		DesignationName=context.getConfiguration().get("Designation");
    		
    	}

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            String[] elements = line.split(",");
            
            if(DesignationName.equalsIgnoreCase(elements[2])){
          //  context.getCounter(COUNTER.MANAGER).increment(1);

          	 outKey.set(elements[2]);

            int i = Integer.parseInt(elements[4]);
            outValue.set(i);
            context.write(outKey, outValue);
        }
            

        }
    }
    
    public static class DesignationPartitioner<K,V> extends Partitioner<K,V>{
    	
    	public int getPartition(K key,V value,int numReduceTasks){
    		
    		if(key.toString().equalsIgnoreCase("Manager"))
    			return 0;
    		else if(key.toString().equalsIgnoreCase("Developer"))
    			return 1;
    		else
    		  	return 2;
    	}
    	   	
    }

    public static class ReduceEmployee extends
            Reducer<Text, IntWritable, Text, IntWritable> {

        // setup, reduce, run, cleanup
        
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }        

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Insufficient args");
           System.exit(-1);
        }
        Configuration conf = new Configuration();

        conf.set("mapred.job.tracker", "hdfs://localhost:50001");
       // conf.setInt("mapreduce.input.lineinputformat.linespermap", 5);
        conf.set("Designation", args[2]);
        Job job = new Job(conf, "Employee Details");

        job.setJarByClass(DistributedCache.class); // class conmtains mapper and
        // reducer class

        job.setMapOutputKeyClass(Text.class); // map output key class
        job.setMapOutputValueClass(IntWritable.class);// map output value class
        job.setOutputKeyClass(Text.class); // output key type in reducer
        job.setOutputValueClass(IntWritable.class);// output value type in
        // reducer

        job.setMapperClass(MapEmployee.class);
        //job.setMapperClass(Mapper.class);
        
        //job.setCombinerClass(ReduceEmployee.class);
        job.setReducerClass(ReduceEmployee.class);
        
       // job.setNumReduceTasks(3);
        //job.setPartitionerClass(DesignationPartitioner.class);
        
        job.setInputFormatClass(TextInputFormat.class); // default -- inputkey
        // type -- longwritable
        // : valuetype is text
        job.setOutputFormatClass(TextOutputFormat.class);
        //job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
       // job.getCounters().findCounter(COUNTER.MANAGER).getValue();
        job.waitForCompletion(true);
        System.out.println();

    }

}