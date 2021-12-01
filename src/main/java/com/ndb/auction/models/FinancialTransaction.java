package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "FinancialTransactions")
public class FinancialTransaction {
    
    private String txnId; // from Conbase API
    private String code;
    private String userId;
    private String transactionType;
    private double cryptoType;
    private double cryptoAmount;
    private double price;
    private Boolean isConfirmed;
    private long createdAt;
    private long confirmedAt;

    public FinancialTransaction() {

    }

    public FinancialTransaction(String type, double cryptoType, double cryptoAmount, double price) {
        this.transactionType = type;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.price = price;
        this.createdAt = System.currentTimeMillis();
        this.isConfirmed = false;
    }

    @DynamoDBAttribute(attributeName = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @DynamoDBRangeKey(attributeName = "txn_id")
    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @DynamoDBHashKey(attributeName = "user_id")
    public String getUserId() {
        return userId;
    }
    

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = "transaction_type")
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    @DynamoDBAttribute(attributeName = "crypto_type")
    public double getCryptoType() {
        return cryptoType;
    }
    public void setCryptoType(double cryptoType) {
        this.cryptoType = cryptoType;
    }
    
    @DynamoDBAttribute(attributeName = "crypto_amount")
    public double getCryptoAmount() {
        return cryptoAmount;
    }
    public void setCryptoAmount(double cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    @DynamoDBAttribute(attributeName = "price")
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "is_confirmed")
    public Boolean getIsConfirmed() {
        return isConfirmed;
    }
    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @DynamoDBAttribute(attributeName = "created_at")
    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDBAttribute(attributeName = "confirmed_at")
    public long getConfirmedAt() {
        return confirmedAt;
    }
    public void setConfirmedAt(long confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
}
