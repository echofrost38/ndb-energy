package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class BidHolding {
    
    private String type;
    private double crypto;
    private double usd; // crypto * price

    public BidHolding() {

    }

    public BidHolding(double crypto, double usd) {
        this.crypto = crypto;
        this.usd = usd;
    }

    @DynamoDBAttribute(attributeName = "crypto")
    public double getCrypto() {
        return crypto;
    }
    public void setCrypto(double crypto) {
        this.crypto = crypto;
    }

    @DynamoDBAttribute(attributeName = "usd")
    public double getUsd() {
        return usd;
    }
    public void setUsd(double usd) {
        this.usd = usd;
    }

}
