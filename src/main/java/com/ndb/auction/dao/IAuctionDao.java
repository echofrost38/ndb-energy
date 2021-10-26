package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.Auction;

/**
 * 1. create new auction round 
 * 2. get all auction list 
 * 3. get auction by round number 
 * 4. update auction round
 * 5. update auction stat when new bid is placed 
 */

public interface IAuctionDao {
	
	Auction createNewAuction(Auction auction);

	List<Auction> getAuctionList();
	
	Auction getAuctionById(String num);
	
	Auction getAuctionByRound(Integer round);

	Auction updateAuctionStats(Auction stats);
	
	Auction updateAuctionByAdmin(Auction auction);
	
	Auction startAuction(Auction auction);
	
	Auction endAuction(Auction auction);
	
}
