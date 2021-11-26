package com.ndb.auction.models.sumsub;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Applicant")
public class Applicant {
	
	private String id;
	private String userId;
	
	public Applicant() {
		
    }

    public Applicant(String userId) {
        this.userId = userId;
    }
	
	@DynamoDBHashKey(attributeName="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
