package com.boaz.practice.hadoop.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.util.ReflectionUtils;

public class StreamCompressByCodec {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
				
		/*------------------------------Compression logic - BEGIN --------------------------*/
		
		//Getting the codec class
		Class<? extends CompressionCodec> codecClass = (Class<? extends CompressionCodec>) Class
				.forName(args[0]);
		Configuration conf = new Configuration();
		
		//Creating instance for that codec
		CompressionCodec codec = ReflectionUtils.newInstance(codecClass, conf);
		
		//Retrieving file systems i.e DFS
		FileSystem fs = FileSystem.get(new URI(args[1]),conf);
		
		//Creating input stream for the existing file to read
		InputStream is = fs.open(new Path(args[1]));		
		
		//Creating output stream to write the compressed file output
		OutputStream os =fs.create(new Path(args[1]));
		
		Compressor compressor=null;
		try{
			//Codec Pools and compressors reuse
			compressor = CodecPool.getCompressor(codec);
			
			//Creating compression output stream from the output stream
			CompressionOutputStream cos = codec.createOutputStream(os,compressor);
			
			//Copying the input stream to the Compressed output stream 
			IOUtils.copyBytes(is, cos, conf);
			
			//Calling to finish the write operation
			cos.finish();
		}finally{
			CodecPool.returnCompressor(compressor);
		}
		
		
		/*------------------------------Compression logic - END --------------------------*/
		
		/*------------------------------Decompression logic - START--------------------------*/
		
		//Input path of the zip file
		Path ipPath = new Path(args[1]);
		
		//Creating codec factory for the util activities
		CompressionCodecFactory ccFactory = new CompressionCodecFactory(conf);
		
		//Creating codec based on the file extension
		CompressionCodec inpCodec = ccFactory.getCodec(ipPath);
		if(inpCodec==null){
			System.err.println("No Codecs Found!!!");
			System.exit(1);
		}
		
		//Removing suffix for the exact output path
		String outputUri = CompressionCodecFactory.removeSuffix(args[2], inpCodec.getDefaultExtension());
		outputUri+="_new";
		
		//Creating compression input stream from the input stream - zipped file
		CompressionInputStream cis = inpCodec.createInputStream(fs.open(ipPath));
		
		//Creating output stream to write the decompressed output
		OutputStream ops = fs.create(new Path(outputUri));
		
		//Copying compression input stream to the output stream 
		IOUtils.copyBytes(cis, ops, conf);
		
		//closing the input stream
		cis.close();
		/*------------------------------Decompression logic - END--------------------------*/
	}

}
