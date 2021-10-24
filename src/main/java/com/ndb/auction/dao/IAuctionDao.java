package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.Auction;

/**
 * 1. create new auction round 
 * 2. get all auction list 
 * 3. get auction by round number 
 * 4. update auction round
 */

public interface IAuctionDao {
	Auction createNewAuction(Auction auction);

	List<Auction> getAuctionList();

	Auction getAuctionById(String num);

	Auction updateAuctionByAdmin(Auction auction);
}
