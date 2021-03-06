package com.boaz.camel.practice.domain.person;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class PersonsResponse {
	private String personName;
	private String personCompanyName;
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getPersonCompanyName() {
		return personCompanyName;
	}
	public void setPersonCompanyName(String personCompanyName) {
		this.personCompanyName = personCompanyName;
	}
}
