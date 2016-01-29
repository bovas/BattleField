package com.boaz.practice.hadoop.hdfs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShowHdfsFileStatusTest {
	
	FileSystem fs;
	MiniDFSCluster miniCluster;
	
	@Before
	public void setup() throws IOException{
		Configuration conf= new Configuration();
		if(System.getProperty("test.build.data")==null){
			System.setProperty("test.build.data", "/tmp");
		}
		miniCluster = new MiniDFSCluster.Builder(conf).build();
		fs = miniCluster.getFileSystem();
		OutputStream os = fs.create(new Path("/hadoop-test-files/filestatus.txt"));
		os.write("content".getBytes("UTF-8"));
		((FSDataOutputStream)os).hflush(); //flushing to memory
		((FSDataOutputStream)os).hsync(); // Writing to disc 
	}
	
	@After
	public void tearDown() throws IOException{
		if(fs!=null)fs.close();
		if(miniCluster!=null)miniCluster.shutdown();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void throwsFileNotFoundForNonExists() throws IOException {
		fs.getFileStatus(new Path("no-such-file"));
	}
	
	@Test
	public void showFileStatus() throws IOException{
		FileStatus status = fs.getFileStatus(new Path("/hadoop-test-files/filestatus.txt"));
		assertThat(status.getPath().toUri().getPath(), is("/hadoop-test-files/filestatus.txt"));
		assertThat(status.isDirectory(), is(false));
		assertThat(status.getLen(), is(7L));
		assertThat(status.getModificationTime(),
		is(lessThanOrEqualTo(System.currentTimeMillis())));
		assertThat(status.getReplication(), is((short) 1));
		assertThat(status.getBlockSize(), is(128 * 1024 * 1024L));
		assertThat(status.getOwner(), is(System.getProperty("user.name")));
		assertThat(status.getGroup(), is("supergroup"));
		assertThat(status.getPermission().toString(), is("rw-r--r--"));
	}
	
	@Test
	public void showDirectoryStatus() throws IOException{
		Path dir = new Path("/hadoop-test-files");
		FileStatus stat = fs.getFileStatus(dir);
		assertThat(stat.getPath().toUri().getPath(), is("/hadoop-test-files"));
		assertThat(stat.isDirectory(), is(true));
		assertThat(stat.getLen(), is(0L));
		assertThat(stat.getModificationTime(),
		is(lessThanOrEqualTo(System.currentTimeMillis())));
		assertThat(stat.getReplication(), is((short) 0));
		assertThat(stat.getBlockSize(), is(0L));
		assertThat(stat.getOwner(), is(System.getProperty("user.name")));
		assertThat(stat.getGroup(), is("supergroup"));
		assertThat(stat.getPermission().toString(), is("rwxr-xr-x"));
	}
}
