package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Crypto_tx")
public class CryptoTransaction {
    public static final int INITIATED = 0;
    public static final int CONFIRMED = 1;
    public static final int CANCELED = 2;

    private String txnId;
    private String roundId;
    private int userId;
    private String code;

    private long amount; // usd
    private long cryptoAmount;
    private String cryptoType;

    private int status;

    private String createdAt;
    private String updatedAt;

    public CryptoTransaction() {

    }

    public CryptoTransaction(String txnId, String roundId, int userId, String code, long amount, long cryptoAmount,
            String cryptoType, String createdAt) {
        this.code = code;
        this.txnId = txnId;
        this.roundId = roundId;
        this.userId = userId;
        this.amount = amount;
        this.cryptoAmount = cryptoAmount;
        this.cryptoType = cryptoType;
        this.createdAt = createdAt;
        this.status = INITIATED;
    }

    @DynamoDBAttribute(attributeName = "txn_id")
    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @DynamoDBAttribute(attributeName = "round_id")
    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    @DynamoDBAttribute(attributeName = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @DynamoDBHashKey(attributeName = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @DynamoDBAttribute(attributeName = "amount")
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @DynamoDBAttribute(attributeName = "crypto_amount")
    public long getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(long cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    @DynamoDBAttribute(attributeName = "crypto_type")
    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    @DynamoDBAttribute(attributeName = "created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDBAttribute(attributeName = "updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @DynamoDBAttribute(attributeName = "istatus")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
