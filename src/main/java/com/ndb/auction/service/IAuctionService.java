package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.Auction;

public interface IAuctionService {
	
	Auction createNewAuction(Auction auction);

	List<Auction> getAuctionList();

	Auction getAuctionById(String num);

	Auction updateAuctionByAdmin(Auction auction);
	
}
