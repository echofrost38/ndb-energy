package com.ndb.auction.models;

import java.util.Date;

/**
 * TODO
 * 1. check status map meaning
 * 2. composite keys
 */

/**
 * status map
 * 0: not confirmed
 * 1 : winner
 * 2 : failed
 * 3 : rejected   
 * 4 : insufficient
 */

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Bid")
public class Bid {

    public static final int CREDIT = 1;
    public static final int CRYPTO = 2;
    public static final int WALLET = 3;
	
	public static final int NOT_CONFIRMED = 0;
	public static final int WINNER 		  = 1;
	public static final int FAILED 		  = 2;
	public static final int REJECTED	  = 3;
    public static final int INSUFFI 	  = 4; 
	
    private String userId;
    private String roundId;
    private Double tokenAmount;
    private Double totalPrice;
    private Double tokenPrice;
    private Integer payType;
    private String cryptoType;
    private Long placedAt;
    private Long updatedAt;
    private Integer status;
    
    public Bid() {
    	
    }
    
    public Bid(String userId, String roundId, Double tokenAmount, Double tokenPrice) {
    	this.userId = userId;
    	this.roundId = roundId;
    	this.tokenAmount = tokenAmount;
    	this.tokenPrice = tokenPrice;
    	this.totalPrice = tokenAmount * tokenPrice;
    	this.placedAt = new Date().getTime();
    	this.updatedAt = this.placedAt;
    	this.status = NOT_CONFIRMED;
    }

    @DynamoDBHashKey(attributeName="user_id")
    @DynamoDBAutoGeneratedKey
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @DynamoDBRangeKey(attributeName="round_id")
    @DynamoDBIndexHashKey(globalSecondaryIndexName="s_round_id")
    public String getRoundId() {
        return roundId;
    }
    public void setRoundId(String roundNumber) {
        this.roundId = roundNumber;
    }
    
    @DynamoDBAttribute(attributeName="token_amount")
    public Double getTokenAmount() {
        return tokenAmount;
    }
    public void setTokenAmount(Double tokenAmount) {
        this.tokenAmount = tokenAmount;
    }
    
    @DynamoDBAttribute(attributeName="total_price")
    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
//    @DynamoDBIndexRangeKey(globalSecondaryIndexName="token_price")
    @DynamoDBAttribute(attributeName="token_price")
    public Double getTokenPrice() {
        return tokenPrice;
    }
    public void setTokenPrice(Double tokenPrice) {
        this.tokenPrice = tokenPrice;
    }
    
    @DynamoDBAttribute(attributeName="placed_at")
    public Long getPlacedAt() {
        return placedAt;
    }
    public void setPlacedAt(Long placedAt) {
        this.placedAt = placedAt;
    }
    
    @DynamoDBAttribute(attributeName="updated_at")
    public Long getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @DynamoDBAttribute(attributeName="status")
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    @DynamoDBAttribute(attributeName="pay_type")
	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

    @DynamoDBAttribute(attributeName="crypto_type")
    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    
}
