package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.Bid;

public interface IBidDao {
	
	Bid placeBid(Bid bid);
	
	List<Bid> getBidListByRound(Integer round_number);
	
	List<Bid> getBidListByRound(String roundId);
	
	List<Bid> getBidListByUser(String userId);
	
	Bid getBid(Integer round, String userId);
	
	Bid getBid(String roundId, String userId);
	
	Bid updateBid(Bid bid);
	
	void updateBidStatus(List<Bid> bids);	
}
