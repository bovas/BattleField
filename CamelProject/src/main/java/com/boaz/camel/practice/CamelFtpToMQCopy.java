package com.boaz.camel.practice;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelFtpToMQCopy {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connFactory = new ActiveMQConnectionFactory("vm://localhost");
		
		CamelContext context = new DefaultCamelContext();
		context.addComponent("jms", JmsComponent.jmsComponent(connFactory));
		
		context.addRoutes(new RouteBuilder() {			
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
		});
		context.start();
		Thread.sleep(10000);
		context.stop();
	}
}
