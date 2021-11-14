package com.ndb.auction.models.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

@DynamoDBDocument
public class Wallet {
	
	private String key;
	private double total;
	private double free;
	private double holding;
	
	public Wallet() {
		this.total = 0.0;
		this.free = 0.0;
		this.holding = 0.0;
	}

	
	@DynamoDBIgnore
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	@DynamoDBAttribute
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	
	@DynamoDBAttribute
	public double getFree() {
		return free;
	}
	public void setFree(double free) {
		this.free = free;
	}
	
	@DynamoDBAttribute
	public double getHolding() {
		return holding;
	}
	public void setHolding(double holding) {
		this.holding = holding;
	}
}
