package com.ndb.auction.models.bid;

import com.ndb.auction.models.BidHolding;

public class BidHoldings {
    
    private String key;
    private BidHolding value;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public BidHolding getValue() {
        return value;
    }
    public void setValue(BidHolding value) {
        this.value = value;
    }
}
