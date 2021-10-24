package com.ndb.auction.models;

/**
 * TODO
 * 1. check status map meaning
 * 2. composite keys
 */

/**
 * status map
 * 0 : winner
 * 1 : failed
 * 2 : rejected   
 * 3 : insufficient
 */

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Bid")
public class Bid {
    
    private Integer userId;
    private Integer roundNumber;
    private Double tokenAmount;
    private Double totalPrice;
    private Double tokenPrice;
    private Long placedAt;
    private Long updatedAt;
    private Integer status;

    
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
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
    public Integer isStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    
}
