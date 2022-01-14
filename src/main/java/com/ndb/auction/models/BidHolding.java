package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class BidHolding {
    
    private Long crypto;
    private Long usd; // crypto * price

    public BidHolding() {

    }

    public BidHolding(Long crypto, Long usd) {
        this.crypto = crypto;
        this.usd = usd;
    }

    @DynamoDBAttribute(attributeName = "crypto")
    public Long getCrypto() {
        return crypto;
    }
    public void setCrypto(Long crypto) {
        this.crypto = crypto;
    }

    @DynamoDBAttribute(attributeName = "usd")
    public Long getUsd() {
        return usd;
    }
    public void setUsd(Long usd) {
        this.usd = usd;
    }

}
