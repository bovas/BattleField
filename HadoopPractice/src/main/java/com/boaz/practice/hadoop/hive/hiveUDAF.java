package com.boaz.practice.hadoop.hive;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;

public class hiveUDAF extends UDAF {
	public static class MeanDoubleUDAFEvaluator implements UDAFEvaluator {
		public static class PartialResult {
			double sum;
			long count;
		}

		private PartialResult partial;

		//intialization
		public void init() {
			partial = null;
		}

		//map method
		public boolean iterate(DoubleWritable value) {
			if (value == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}
			partial.sum += value.get();
			partial.count++;
			return true;
		}

		
		// It works @Mapper Output 
		public PartialResult terminatePartial() {
			return partial;
		}

		// Combiner Phase + reducer
		public boolean merge(PartialResult other) {
			if (other == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}
			partial.sum += other.sum;
			partial.count += other.count;
			return true;
		}
		

		// Rduce Phase
		public DoubleWritable terminate(){
			
			if (partial == null) {
				return null;
				}
				return new DoubleWritable(partial.sum / partial.count);

		}
	}
}