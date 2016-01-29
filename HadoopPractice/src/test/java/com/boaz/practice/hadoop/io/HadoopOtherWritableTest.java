package com.boaz.practice.hadoop.io;

import static com.boaz.practice.hadoop.io.HadoopSerializerUtils.serializeToString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.io.Text;
import org.junit.Test;

public class HadoopOtherWritableTest {
	@Test
	private void testTextWritable() throws IOException{
		//utf8 encoding
		Text t = new Text("hadoop");
		assertThat(t.getLength(), is(6));
		assertThat(t.getBytes().length, is(6));

		//charAt
		assertThat(t.charAt(2), is((int) 'd'));		//returns int value ,unlike string returns char
		assertThat("Out of bounds", t.charAt(100), is(-1));
		
		//find
		assertThat("Find a substring", t.find("do"), is(2));	//equals to index of
	    assertThat("Finds first 'o'", t.find("o"), is(3));
	    assertThat("Finds 'o' from position 4 or later", t.find("o", 4), is(4));
	    assertThat("No match", t.find("pig"), is(-1));
	    
	    //mutability
	    t.set("pig");
	    assertThat(t.getLength(), is(3));
	    assertThat(t.getBytes().length, is(3));
	    
	    //ByteArrayNotShortened
	    t = new Text("hadoop");	// resetting
	    t.set(new Text("pig"));
	    assertThat(t.getLength(), is(3));
	    assertThat("Byte length not shortened", t.getBytes().length,is(6));		//valid bytes length - 3
	    
	    assertThat(new Text("hadoop").toString(), is("hadoop")); //to string method
	    
	    assertThat("\ud800\udc00".compareTo("\ue000"), lessThan(0));	//comparison
	    assertThat(new Text("\ud800\udc00").compareTo(new Text("\ue000")), greaterThan(0));
	    
	    //iterating text
	    t = new Text("\u0041\u00DF\u6771\uD801\uDC00");
	    ByteBuffer buf = ByteBuffer.wrap(t.getBytes(), 0, t.getLength());
	    int cp;
	    while (buf.hasRemaining() && (cp = Text.bytesToCodePoint(buf)) != -1) {
	      System.out.println(Integer.toHexString(cp));
	    }
	} 
	
	@Test
	private void testTextWritableVsString() throws IOException{
		String s = "\u0041\u00DF\u6771\uD801\uDC00";
		assertThat(s.length(), is(5));
		assertThat(s.getBytes("UTF-8").length, is(10));

		assertThat(s.indexOf('\u0041'), is(0));
		assertThat(s.indexOf('\u00DF'), is(1));
		assertThat(s.indexOf('\u6771'), is(2));
		assertThat(s.indexOf('\uD801'), is(3));
		assertThat(s.indexOf('\uDC00'), is(4));

		assertThat(s.charAt(0), is('\u0041'));
		assertThat(s.charAt(1), is('\u00DF'));
		assertThat(s.charAt(2), is('\u6771'));
		assertThat(s.charAt(3), is('\uD801'));
		assertThat(s.charAt(4), is('\uDC00'));

		//non supplementary chars - indexed by bytes offset
		Text t = new Text("\u0041\u00DF\u6771\uD801\uDC00");		    
		assertThat(serializeToString(t), is("0a41c39fe69db1f0909080"));	    
		assertThat(t.charAt(t.find("\u0041")), is(0x0041));	//index of 0 - 1 byte
		assertThat(t.charAt(t.find("\u00DF")), is(0x00DF)); //index of 1 - 2 bytes
		assertThat(t.charAt(t.find("\u6771")), is(0x6771)); //index of 3 - 3 bytes
		assertThat(t.charAt(t.find("\uD801\uDC00")), is(0x10400)); //index of 6 - 4 bytes
	}
	
	@Test
	private void testBytesWritable() {
		

	}
}
