package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

@DynamoDBDocument
public class Wallet {
	
	private String key;
	private long total;
	private long free;
	private long holding;
	
	public Wallet() {
	}

	public Wallet(String key, long free, long hold) {
		this.key = key;
		this.free = free;
		this.holding = hold;
		this.total = free + hold;
	}

	@DynamoDBIgnore
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	@DynamoDBAttribute
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	@DynamoDBAttribute
	public long getFree() {
		return free;
	}
	public void setFree(long free) {
		this.free = free;
	}
	
	@DynamoDBAttribute
	public long getHolding() {
		return holding;
	}
	public void setHolding(long holding) {
		this.holding = holding;
	}
}
