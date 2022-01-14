package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AuctionStats {

	private Long qty;
	private Long win;
	private Long fail;

	public AuctionStats() {	
		this.qty = 0L;
		this.win = 0L;
		this.fail = 0L;
	}

	public AuctionStats(Long qty, Long win, Long fail) {
		this.qty = qty;
		this.win = win;
		this.fail = fail;
	}

}
