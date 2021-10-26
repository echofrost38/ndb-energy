package com.ndb.auction.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODOs: 
 * 1. primary key id when create new auction
 * 2. Map type or DynamoDB Document
 */

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Auction")
public class Auction extends BaseModel {
	
	// Auction status constants
	public static final int PENDING = 0;
	public static final int STARTED = 1;
	public static final int ENDED   = 2;

    private String auctionId;
    private Integer number;
    private Long startedAt;
    private Long endedAt;
    private Double totalToken;
    private Double minPrice;
    private Double sold;
    private AuctionStats stats;
    private Integer status;
    
    public Auction() {
    	
    }
    
    public Auction(Integer _number, String _startedAt, Double _totalToken, Double _minPrice) {
    	this.number = _number;
    	this.totalToken = _totalToken;
    	this.minPrice = _minPrice;
    	this.sold = 0.0;
    	
    	// cast String date time to Long epoch
    	// Date Format : 2021-10-24T12:00:00.000-0000
    	// check null
    	if(_startedAt != null) {
	    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	    	try {
	    	    Date d = f.parse(_startedAt);
	    	    long startedAtMill = d.getTime();
	    	    long endedAtMill = startedAtMill + 24L * 60L * 60L * 1000L;
	    	    this.startedAt = startedAtMill;
	    	    this.endedAt = endedAtMill;
	    	} catch (ParseException e) {
	    	    e.printStackTrace();
	    	}
    	}
    	// initial pending status
    	this.status = PENDING; 
    	AuctionStats auctionStats = new AuctionStats();
    	this.stats = auctionStats;
    }

    @DynamoDBHashKey(attributeName="id")
    @DynamoDBAutoGeneratedKey
    public String getAuctionId() {
        return auctionId;
    }
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    @DynamoDBIndexHashKey(attributeName="number", globalSecondaryIndexName="s_number")
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

    @DynamoDBAttribute(attributeName="started_at")
    public Long getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    @DynamoDBAttribute(attributeName="ended_at")
    public Long getEndedAt() {
        return endedAt;
    }
    public void setEndedAt(Long endedAt) {
        this.endedAt = endedAt;
    }

    @DynamoDBAttribute(attributeName="total_token")
    public Double getTotalToken() {
        return totalToken;
    }
    public void setTotalToken(Double totalToken) {
        this.totalToken = totalToken;
    }

    @DynamoDBAttribute(attributeName="min_price")
    public Double getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    @DynamoDBAttribute(attributeName="sold")
    public Double getSold() {
        return sold;
    }
    public void setSold(Double sold) {
        this.sold = sold;
    }

    @DynamoDBAttribute(attributeName="stats")
    public AuctionStats getStats() {
        return stats;
    }
    public void setStats(AuctionStats stats) {
        this.stats = stats;
    }

    @DynamoDBAttribute(attributeName="status")
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
