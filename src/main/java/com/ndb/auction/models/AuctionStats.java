package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class AuctionStats {
	private Double qty;
	private Double win;
	private Double fail;
	
	public AuctionStats() {
		this.qty = 0.0;
		this.win = 0.0;
		this.fail = 0.0;
	}
	
	public AuctionStats(double qty, double win, double fail) {
		this.qty = qty;
		this.win = win;
		this.fail = fail;
	}

	@DynamoDBAttribute(attributeName = "qty")
	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	@DynamoDBAttribute(attributeName = "win")
	public Double getWin() {
		return win;
	}

	public void setWin(Double win) {
		this.win = win;
	}

	@DynamoDBAttribute(attributeName = "fail")
	public Double getFail() {
		return fail;
	}

	public void setFail(Double fail) {
		this.fail = fail;
	}
}
