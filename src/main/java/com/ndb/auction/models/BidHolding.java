package com.ndb.auction.models;

public class BidHolding {
    
    private Double crypto;
    private Double usd; // crypto * price

    public BidHolding() {

    }

    public BidHolding(Double crypto, Double usd) {
        this.crypto = crypto;
        this.usd = usd;
    }

    public Double getCrypto() {
        return crypto;
    }
    public void setCrypto(Double crypto) {
        this.crypto = crypto;
    }

    public Double getUsd() {
        return usd;
    }
    public void setUsd(Double usd) {
        this.usd = usd;
    }

}
