package com.boaz.practice.hadoop.hdfs;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

public class HDFSFileOperation {
	static{
		//URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}
	public static void main(String[] args) throws IOException {
		//doSimpleUrlRead();
		//doSimpleHDFSRead();
		//copyFromLocalToHdfs();
		listFileOrDirStatus();
	}
	
	private static void listFileOrDirStatus() throws IOException {
		Configuration conf = new Configuration();
		String dest = "hdfs://localhost/hadoop-test-files";
		FileSystem fs = FileSystem.get(URI.create("hdfs://localhost/"), conf);
		FileStatus[] fileStatus = fs.listStatus(new Path[]{new Path(dest)});
		Path[] listedPaths = FileUtil.stat2Paths(fileStatus);
		for (Path p : listedPaths) {
			System.out.println(p);
		}

	}

	private static void copyFromLocalToHdfs() throws IOException {
		
		String localFile="/home/boaz/git/hadoop-book/input/docs/1400-8.txt";
		InputStream in = new BufferedInputStream(new FileInputStream(localFile));
		
		Configuration conf = new Configuration();
		String dest = "hdfs://localhost/hadoop-test-files/1400-8.txt";
		FileSystem fs = FileSystem.get(URI.create(dest ), conf);
		OutputStream ods = null;
		try {
			ods = fs.create(new Path(dest), new Progressable() {
				
				public void progress() {
					System.out.print(".");
					
				}
			});
			IOUtils.copyBytes(in, ods, 4096,false);
		}finally{
			IOUtils.closeStream(ods);
		}
	}

	private static void doSimpleUrlRead() throws IOException{
		InputStream in = null;
		try {
			in = new URL("hdfs://localhost/hadoop-test-files/Hadoop-test").openStream();
			IOUtils.copyBytes(in, System.out, 4096, false);
		} finally{
			IOUtils.closeStream(in);
		}
	}
	private static void doSimpleHDFSRead() throws IllegalArgumentException, IOException{
		DataInputStream in = null;
		String hdfsPath = "hdfs://localhost/hadoop-test-files/1400-8.txt";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf );
		try {
			in = fs.open(new Path(hdfsPath));
			IOUtils.copyBytes(in, System.out, 4096,false);
						
			FSDataInputStream fsin = (FSDataInputStream) in;
			
			//Seekable
			System.out.println(fsin.getPos());
			fsin.seek(5); // go back to the start of the file
			System.out.println(fsin.getPos());
			IOUtils.copyBytes(in, System.out, 4096, false);
			
			//PositionedReadable
			byte[] byt = new byte[1024];
			fsin.read(byt, 0, byt.length);			
			System.out.println(byt);
		}finally{
			IOUtils.closeStream(in);
		}
	}
}
