package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class BidHolding {
    
    private Double crypto;
    private Double usd; // crypto * price

    public BidHolding() {

    }

    public BidHolding(Double crypto, Double usd) {
        this.crypto = crypto;
        this.usd = usd;
    }

    @DynamoDBAttribute(attributeName = "crypto")
    public Double getCrypto() {
        return crypto;
    }
    public void setCrypto(Double crypto) {
        this.crypto = crypto;
    }

    @DynamoDBAttribute(attributeName = "usd")
    public Double getUsd() {
        return usd;
    }
    public void setUsd(Double usd) {
        this.usd = usd;
    }

}
