package com.ndb.auction.models.p2pb2b;
import com.fasterxml.jackson.annotation.JsonProperty;

public class P2PB2BResponse{
    public boolean success;
    public String errorCode;
    public String message;
    public Result result;
    public double cache_time;
    public double current_time;
}
