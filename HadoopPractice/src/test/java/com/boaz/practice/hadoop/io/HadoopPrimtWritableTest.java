package com.boaz.practice.hadoop.io;

import static com.boaz.practice.hadoop.io.HadoopSerializerUtils.deSerialize;
import static com.boaz.practice.hadoop.io.HadoopSerializerUtils.serialize;
import static com.boaz.practice.hadoop.io.HadoopSerializerUtils.serializeToString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.util.StringUtils;
import org.junit.Test;

public class HadoopPrimtWritableTest {
	
	@Test
	public void testVIntPrimWritable() throws IOException{
		byte[] data = serialize(new VIntWritable(163));
		assertThat(StringUtils.byteToHexString(data), is("8fa3"));
		
		//testing the size of encoding value - use it for non uniform values
		assertThat(serializeToString(new VIntWritable(1)), is("01")); // 1 byte
	    assertThat(serializeToString(new VIntWritable(-112)), is("90")); // 1 byte
	    assertThat(serializeToString(new VIntWritable(127)), is("7f")); // 1 byte
	    assertThat(serializeToString(new VIntWritable(128)), is("8f80")); // 2 byte
	    assertThat(serializeToString(new VIntWritable(163)), is("8fa3")); // 2 byte
	    assertThat(serializeToString(new VIntWritable(Integer.MAX_VALUE)),
	        is("8c7fffffff")); // 5 byte
	    assertThat(serializeToString(new VIntWritable(Integer.MIN_VALUE)),
	        is("847fffffff")); // 5 byte
	}
	
	@Test
	public void testIntWritable() throws IOException {		
		//serialize test
		IntWritable writable = new IntWritable(200);
		byte[] bytes = serialize(writable);
		assertThat(bytes.length, is(4));
		assertThat(StringUtils.byteToHexString(bytes), is("000000a3")); //not working now
				
		//deserialize test
		IntWritable intWritable = new IntWritable();
		deSerialize(intWritable, bytes);
		assertThat(intWritable.get(),is(200));	
	}
		
	@SuppressWarnings("unchecked")
	@Test
	public void comparator() throws IOException{
		RawComparator<IntWritable> comparator = WritableComparator.get(IntWritable.class);
		
		//Object comparision
		IntWritable w1 = new IntWritable(163);
		IntWritable w2 = new IntWritable(67);
		assertThat(comparator.compare(w1, w2), greaterThan(0));
		
		//Byte Comparision - Hadoop's advantage
		byte[] b1 = serialize(w1);
		byte[] b2 = serialize(w2);
		assertThat(comparator.compare(b1, 0, b1.length, b2, 0, b2.length),greaterThan(0));
	}

}
