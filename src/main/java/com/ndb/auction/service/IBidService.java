package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.Bid;

public interface IBidService {
	
	// place new bid
	Bid placeNewBid(String userId, Integer roundNumber, Double tokenAmount, Double tokenPrice, String payType);
	
	// get Bid List
	List<Bid> getBidListByRound(Integer round);
	
	// get Bid List by user
	List<Bid> getBidListByUser(String userId);
	
	// get Bid 
	Bid getBid(Integer round, String userId);
	
	// update Bid
	Bid updateBid(String userId, Integer roundNumber, Double tokenAmount, Double tokenPrice);
	
	// called when new bid is placed
	void updateBidRanking(String userId, Integer roundNumber);
	
}
