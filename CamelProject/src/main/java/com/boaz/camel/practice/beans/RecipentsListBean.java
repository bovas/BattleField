package com.boaz.camel.practice.beans;

import org.apache.camel.RecipientList;
import org.apache.camel.language.XPath;
import org.springframework.stereotype.Component;

@Component
public class RecipentsListBean {
	@RecipientList
	public String[] route(@XPath("/person/@customer") String customer) {
		System.out.println("Customer name::" + customer);
		if (isGoldCustomer(customer)) {
			return new String[] { "jms:accounting", "jms:production" };
		} else {
			return new String[] { "jms:accounting" };
		}
	}

	private boolean isGoldCustomer(String customer) {
		return customer.equals("honda");
	}
}
