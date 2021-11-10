package com.ndb.auction.payload;

import java.util.Map;

import com.ndb.auction.models.coinbase.CoinbasePricing;

public class CryptoPayload {
    
    private Map<String, String> addresses;
    private Map<String, CoinbasePricing> pricing;

    public Map<String, String> getAddresses() {
        return addresses;
    }
    public void setAddresses(Map<String, String> addresses) {
        this.addresses = addresses;
    }
    public Map<String, CoinbasePricing> getPricing() {
        return pricing;
    }
    public void setPricing(Map<String, CoinbasePricing> pricing) {
        this.pricing = pricing;
    }
    
}
