package com.boaz.practice.hadoop.spark;

import java.util.Arrays;
import javax.xml.soap.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.rdd.HadoopRDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.util.HdfsUtils;

public class SparkFileReadAndAppend {

	public static void main(String[] args) {
		SparkConf sparkConf = new SparkConf().setAppName("HdfsReadAppend");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(2000));

		// Facing issues in below line
		JavaReceiverInputDStream<String> lines = jssc.fileStream("", LongWritable.class, Text.class,
				TextInputFormat.class);
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String x) {
				return Arrays.asList(x.split(" "));
			}
		});
		JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2<String, Integer>(s, 1);
			}
		});
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		});
		// wordCounts.foreach(new Function<JavaPairRDD<String,Integer>, Void>()
		// {
		//
		// @Override
		// public Void call(JavaPairRDD<String, Integer> stringIntegerTuple2)
		// throws Exception {
		// System.out.println(String.format("%s - %d",
		// stringIntegerTuple2.toString(), stringIntegerTuple2.toArray()));
		// return null;
		//
		// }
		// });
		wordCounts.print();

		jssc.start(); // Start the computation
		jssc.awaitTermination();
	}
}
