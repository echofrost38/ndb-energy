package com.ndb.auction.service.interfaces;

import java.util.List;

import com.ndb.auction.models.Auction;

public interface IAuctionService {
	
	/* General privilege */
	List<Auction> getAuctionList();

	Auction getAuctionById(String num);
	
	/* Admin privilege */
	Auction createNewAuction(Auction auction);
	
	Auction updateAuctionByAdmin(Auction auction);
	
	Auction startAuction(String id);
	
	// called from scheduled tasks
	Auction endAuction(String id);
}
