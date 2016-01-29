package com.boaz.practice.hadoop.pig;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pig.EvalFunc;
import org.apache.pig.PigException;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;

public class max extends EvalFunc<Integer> {

	@Override
	public Integer exec(Tuple input) throws IOException {
		try {
			DataBag bag = (DataBag) input.get(0);
			if (bag == null)
				return null;

			int max_value = Integer.MIN_VALUE;
			for (Iterator<Tuple> it = bag.iterator(); it.hasNext();) {
				Tuple t = it.next();
				if (max_value < (Integer) t.get(0)) {
					max_value = (Integer) t.get(0);
				}
			}
			return max_value;
		} catch (ExecException ee) {
			throw ee;
		} catch (Exception e) {
			int errCode = 2106;
			String msg = "Error while computing maximum in "
					+ this.getClass().getSimpleName();
			throw new ExecException(msg, errCode, PigException.BUG, e);
		}
	}
}