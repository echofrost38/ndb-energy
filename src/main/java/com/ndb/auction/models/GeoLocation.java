package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="GeoLocation")
public class GeoLocation {
	
	private String countryCode;
	private boolean isAllowed;
	
	public GeoLocation() {
		
	}
	
	public GeoLocation(String code, boolean allowed) {
		this.countryCode = code;
		this.isAllowed = allowed;
	}

	@DynamoDBHashKey(attributeName="country_code")
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	@DynamoDBAttribute(attributeName="is_allowed")
	public boolean isAllowed() {
		return isAllowed;
	}

	public void setAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

}
