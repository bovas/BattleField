package com.boaz.practice.hadoop.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.StringUtils;

public class HadoopSerializerUtils {
	public static byte[] serialize(Writable writable) throws IOException{
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteArray);
		writable.write(out);
		out.close();
		return byteArray.toByteArray();
	}
	public static byte[] deSerialize(Writable writable,byte[] bytes) throws IOException{
		ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(byteArray);
		writable.readFields(dis);
		dis.close();
		return bytes;
	}
	public static String serializeToString(Writable src) throws IOException {
		return StringUtils.byteToHexString(serialize(src));
	}

	public static String writeTo(Writable src, Writable dest) throws IOException {
		byte[] data = deSerialize(dest, serialize(src));
		return StringUtils.byteToHexString(data);
	}
}
