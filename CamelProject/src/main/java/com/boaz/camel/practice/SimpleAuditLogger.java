package com.boaz.camel.practice;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SimpleAuditLogger implements Processor {

	public void process(Exchange exchange) throws Exception {
		System.out.println("body of the message::"+exchange.getIn().getBody());
	}

}
