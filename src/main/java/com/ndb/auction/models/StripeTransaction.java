package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="fiat_tx")
public class StripeTransaction {
	
	public static final Integer INITIATED = 0;
	public static final Integer AUTHORIZED = 1;
	public static final Integer CAPTURED = 2;
	public static final Integer CANCELED = 3;
	
	// #1
	private String roundId;
	private String userId;

	// #2
	// private String bidId;

	private String paymentIntentId;
	private Long amount;
	private Integer status;
	private Long createdAt;
	
	public StripeTransaction() {
		
	}
	
	public StripeTransaction(String roundId, String userId, Long amount, String paymentIntentId) {
		this.roundId = roundId;
		this.userId = userId;
		this.amount = amount;
		this.paymentIntentId = paymentIntentId;
		this.createdAt = System.currentTimeMillis();
		this.status = INITIATED;
	}
	
	@DynamoDBAttribute(attributeName="round_id")
	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}
	
	@DynamoDBAttribute(attributeName="user_id")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBAttribute(attributeName="payment_intent")
	public String getPaymentIntentId() {
		return paymentIntentId;
	}

	public void setPaymentIntentId(String paymentIntentId) {
		this.paymentIntentId = paymentIntentId;
	}
	
	@DynamoDBAttribute(attributeName="amount")
	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
	@DynamoDBAttribute(attributeName="status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@DynamoDBAttribute(attributeName="created_at")
	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

}
