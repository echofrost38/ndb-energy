package com.ndb.auction.models.coinbase;

import java.util.List;
import java.util.Map;

public class CoinbaseEventData {
    private String code;
    private String name;
    private String description;
    private String hosted_url;
    private String created_at;
    private String expires_at;
    private List<CoinbaseTimeline> timeline;
    private Object metadata;
    private String pricing_type;
    private List<CoinbasePayments> payments;
    private Map<String, String> addresses;


    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getHosted_url() {
        return hosted_url;
    }
    public void setHosted_url(String hosted_url) {
        this.hosted_url = hosted_url;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getExpires_at() {
        return expires_at;
    }
    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }
    public List<CoinbaseTimeline> getTimeline() {
        return timeline;
    }
    public void setTimeline(List<CoinbaseTimeline> timeline) {
        this.timeline = timeline;
    }
    public Object getMetadata() {
        return metadata;
    }
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    public String getPricing_type() {
        return pricing_type;
    }
    public void setPricing_type(String pricing_type) {
        this.pricing_type = pricing_type;
    }
    public List<CoinbasePayments> getPayments() {
        return payments;
    }
    public void setPayments(List<CoinbasePayments> payments) {
        this.payments = payments;
    }
    public Map<String, String> getAddresses() {
        return addresses;
    }
    public void setAddresses(Map<String, String> addresses) {
        this.addresses = addresses;
    }

    
}
