package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AuctionStats {

	private long qty;
	private long win;
	private long fail;

	public AuctionStats() {
	}

	public AuctionStats(long qty, long win, long fail) {
		this.qty = qty;
		this.win = win;
		this.fail = fail;
	}

}
