package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class BidHolding {
    
    private long crypto;
    private long usd; // crypto * price

    public BidHolding() {

    }

    public BidHolding(long crypto, long usd) {
        this.crypto = crypto;
        this.usd = usd;
    }

    @DynamoDBAttribute(attributeName = "crypto")
    public long getCrypto() {
        return crypto;
    }
    public void setCrypto(long crypto) {
        this.crypto = crypto;
    }

    @DynamoDBAttribute(attributeName = "usd")
    public long getUsd() {
        return usd;
    }
    public void setUsd(long usd) {
        this.usd = usd;
    }

}
