package com.boaz.camel.practice;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelFileSimpleCopier {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {			
			@Override
			public void configure() throws Exception {
				from("file:data/input").to("file:data/output");				
			}
		});
		context.start();
		Thread.sleep(10000);
		context.stop();
	}

}
