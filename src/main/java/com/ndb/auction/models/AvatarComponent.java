package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Avatar_component")
public class AvatarComponent {
	
	private String groupId;
	private Integer compId;
	private Integer tierLevel;
	private Double price;

	public AvatarComponent() {
		
	}
	
	public AvatarComponent(String groupId, Integer compId, Integer tierLevel, Double price) {
		this.groupId = groupId;
		this.compId = compId;
		this.tierLevel = tierLevel;
		this.price = price;
	}
	
	@DynamoDBHashKey(attributeName="id")
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@DynamoDBIndexHashKey(attributeName="comp_id")
	public Integer getCompId() {
		return compId;
	}

	public void setCompId(Integer compId) {
		this.compId = compId;
	}
	
	@DynamoDBAttribute(attributeName="tier_level")
	public Integer getTierLevel() {
		return tierLevel;
	}

	public void setTierLevel(Integer tierLevel) {
		this.tierLevel = tierLevel;
	}
	
	@DynamoDBAttribute(attributeName="price")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
