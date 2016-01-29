package com.boaz.practice.hadoop.mr;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DFSClient.Conf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class MaxTemperatureTest {

	//Mapper test method	
	@Test
	public void processValidRecord() throws IOException{
		Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" +
				// Year ^^^^
				"99999V0203201N00261220001CN9999999N9-00111+99999999999");
				// Temperature ^^^^^
		new MapDriver<LongWritable, Text, Text, IntWritable>()
			.withMapper(new MaxTemperatureMR.MaxTemperatureMapper())
			.withInput(new LongWritable(0), value)
			.withOutput(new Text("1950"),new IntWritable(-11))
			.runTest();

	}
	
	//Reducer test method
	@Test
	public void returnMaxIntRecords()throws IOException{
		new ReduceDriver<Text, IntWritable, Text, IntWritable>()
		.withReducer(new MaxTemperatureMR.MaxTemperatureReducer())
		.withInput(new Text("1950"), Arrays.asList(new IntWritable(10), new IntWritable(5)))
		.withOutput(new Text("1950"), new IntWritable(10))
		.runTest();
	}
	
	//Driver test method
	@Test
	public void testDriver() throws Exception{
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.framework.name", "local");
		conf.setInt("mapreduce.task.io.sort.mb", 1);
		
		Path input = new Path("input/ncdc/micro/sample.txt");
		Path output = new Path("output");
		
		FileSystem fs = FileSystem.getLocal(conf);
		fs.delete(output, true); // delete old output
		MaxTemperatureMR driver = new MaxTemperatureMR();
		driver.setConf(conf);
		int exitCode = driver.run(new String[] {
		input.toString(), output.toString() });
		assertThat(exitCode, is(0));
		checkOutput(conf, output);
	}
	
	private void checkOutput(Configuration conf, Path output) throws IOException {
	    FileSystem fs = FileSystem.getLocal(conf);
	    Path[] outputFiles = FileUtil.stat2Paths(
	        fs.listStatus(output, new OutputLogFilter()));
	    assertThat(outputFiles.length, is(1));
	    
	    BufferedReader actual = asBufferedReader(fs.open(outputFiles[0]));
	    BufferedReader expected = asBufferedReader(
	        getClass().getResourceAsStream("/expected.txt"));
	    String expectedLine;
	    while ((expectedLine = expected.readLine()) != null) {
	      assertThat(actual.readLine(), is(expectedLine));
	    }
	    assertThat(actual.readLine(), nullValue());
	    actual.close();
	    expected.close();
	  }
	  
	public static class OutputLogFilter implements PathFilter {
		public boolean accept(Path path) {
			return !path.getName().startsWith("_");
		}
	}

	private BufferedReader asBufferedReader(InputStream in) throws IOException {
		return new BufferedReader(new InputStreamReader(in));
	}
}
