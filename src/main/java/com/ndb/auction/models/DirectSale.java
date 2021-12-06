package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class DirectSale {

    private String userId;
    private String txnId;

    // stripe and coinbase, ndb wallet
    private int payType;
    private double ndbPrice;
    private double ndbAmount;

    private boolean isConfirmed;

    private long createdAt;
    private long confirmedAt;

    // for stripe
    private String paymentIntentId;

    // for coinbase
    private String code;
    private String cryptoType;
    private double cryptoPrice;
    private double cryptoAmount;

    public DirectSale () {

    }

    public DirectSale(
        String userId,
        String txnId,
        double ndbPrice,
        double ndbAmount
    ) {
        this.userId = userId;
        this.txnId = txnId;
        this.ndbPrice = ndbPrice;
        this.ndbAmount = ndbAmount;

        this.isConfirmed = false;
        this.createdAt = System.currentTimeMillis();
    }

    @DynamoDBHashKey(attributeName = "user_id")
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = "txn_id")
    public String getTxnId() {
        return txnId;
    }
    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @DynamoDBAttribute(attributeName = "pay_type")
    public int getPayType() {
        return payType;
    }
    public void setPayType(int payType) {
        this.payType = payType;
    }

    @DynamoDBAttribute(attributeName = "ndb_price")
    public double getNdbPrice() {
        return ndbPrice;
    }
    public void setNdbPrice(double ndbPrice) {
        this.ndbPrice = ndbPrice;
    }

    @DynamoDBAttribute(attributeName = "ndb_amount")
    public double getNdbAmount() {
        return ndbAmount;
    }
    public void setNdbAmount(double ndbAmount) {
        this.ndbAmount = ndbAmount;
    }

    @DynamoDBAttribute(attributeName = "is_confirmed")
    public boolean isConfirmed() {
        return isConfirmed;
    }
    public void setConfirmed(boolean isConfirmed) {
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

    @DynamoDBAttribute(attributeName = "payment_intent_id")
    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    @DynamoDBAttribute(attributeName = "code")
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @DynamoDBAttribute(attributeName = "crypto_type")
    public String getCryptoType() {
        return cryptoType;
    }
    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    @DynamoDBAttribute(attributeName = "crypto_price")
    public double getCryptoPrice() {
        return cryptoPrice;
    }
    public void setCryptoPrice(double cryptoPrice) {
        this.cryptoPrice = cryptoPrice;
    }

    @DynamoDBAttribute(attributeName = "crypto_amount")
    public double getCryptoAmount() {
        return cryptoAmount;
    }
    public void setCryptoAmount(double cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }
}
