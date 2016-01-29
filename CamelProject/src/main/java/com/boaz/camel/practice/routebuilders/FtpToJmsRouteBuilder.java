package com.boaz.camel.practice.routebuilders;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class FtpToJmsRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("ftp://rider.com/orders?username=rider&password=secret")
		.process(new Processor() {					
			public void process(Exchange exchange) throws Exception {
				System.out.println("Downloaded file name"+exchange.getIn().getHeader("camelFileName"));
			}
		})
		.to("jms:queue:incomingOrders");
	}

}
