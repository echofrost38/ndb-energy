package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Notification_Type")
public class NotificationType {

	private Integer id; 
    private String name;
    private Integer value;

    public NotificationType() {
        this.id = 1; 
    }

    @DynamoDBHashKey(attributeName = "id")
	public Integer getId() {
		return id;
	}
    public void setId(Integer id) {
        this.id = id;
    }
    
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "value")
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
}
