package com.ndb.auction.models.p2pb2b;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Result{
    public String bid;
    public String ask;
    @JsonProperty("open")
    public String myopen;
    public String high;
    public String low;
    public String last;
    public String volume;
    public String deal;
    public String change;
}
