package com.ndb.auction.models.coinbase;

public class CoinbaseEventBody {
    
    private String id;
    private String resource;
    private String type;
    private String api_version;
    private String created_at;
    private CoinbaseEventData data;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getApi_version() {
        return api_version;
    }
    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public CoinbaseEventData getData() {
        return data;
    }
    public void setData(CoinbaseEventData data) {
        this.data = data;
    }

    
}
