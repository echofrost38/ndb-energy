package com.ndb.auction.models.coinbase;

import java.util.Map;

public class CoinbasePayments {
    
    private String network;
    private String transaction_id;
    private String status;
    
    private Map<String, CoinbasePricing> value;
    private Object block;

    public Object getBlock() {
        return block;
    }
    public void setBlock(Object block) {
        this.block = block;
    }

    public String getNetwork() {
        return network;
    }
    public void setNetwork(String network) {
        this.network = network;
    }
    public String getTransaction_id() {
        return transaction_id;
    }
    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Map<String, CoinbasePricing> getValue() {
        return value;
    }
    public void setValue(Map<String, CoinbasePricing> value) {
        this.value = value;
    }

    
}
