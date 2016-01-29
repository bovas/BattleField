package com.boaz.practice.hadoop.mr;

import org.apache.hadoop.io.Text;

public class NcdcRecordParser {
	
	private static final int MISSING = 9999;
	
	private String year;
	private int airTemperature;
	private String quality;
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getAirTemperature() {
		return airTemperature;
	}

	public void setAirTemperature(int airTemperature) {
		this.airTemperature = airTemperature;
	}	
	
	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public void parse(Text record){
		parse(record.toString());
	}

	private void parse(String line) {
		year = line.substring(15, 19);		
		if(line.charAt(87)=='+'){
			airTemperature = Integer.parseInt(line.substring(88, 92));
		} else {
			airTemperature = Integer.parseInt(line.substring(87, 92));
		}
		quality = line.substring(92,93);
	}
	public boolean isValidTemperature() {
		return airTemperature != MISSING && quality.matches("[01459]");
	}

}
