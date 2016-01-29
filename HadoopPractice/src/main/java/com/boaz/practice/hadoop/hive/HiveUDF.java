package com.boaz.practice.hadoop.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class HiveUDF extends UDF {
	private Text result = new Text();

	
	public Text evaluate(Text Name) {
		
		if (Name == null) {
			return null;
		}
		
		String res = Name.toString().toUpperCase();
		
		
		result = new Text(res);
		
		return result;
	} 
	

	public Text evaluate(IntWritable id, Text Name) {
		if(id == null)
		{
			return null;
		}
		if (Name == null) {
			return null;
		}
		
		String res = Integer.toString(id.get())+"-"+Name.toString();
		
		
		result = new Text(res);
		
		return result;
	}
}