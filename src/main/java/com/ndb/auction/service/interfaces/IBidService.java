package com.ndb.auction.service.interfaces;

import java.util.List;

import com.ndb.auction.models.Bid;

public interface IBidService {
	
	// place new bid
	Bid placeNewBid(String userId, String roundId, Double tokenAmount, Double tokenPrice, Integer payType, String cryptoType);
	
	Bid increaseBid(String userId, String roundId, Double tokenAmount, Double tokenPrice, Integer payType, String cryptoType);

	// get Bid List
	List<Bid> getBidListByRound(Integer round);
	
	// get Bid List by user
	List<Bid> getBidListByUser(String userId);
	
	// get Bid 
	Bid getBid(Integer round, String userId);
	
	// update Bid
	Bid updateBid(String userId, String roundId, Double tokenAmount, Double tokenPrice);
	
	/**
	 * It is called from Payment service with user id and round id.
	 */
	void updateBidRanking(String userId, String roundId);
	
}
