package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Bid;

@Service
public class BidService extends BaseService implements IBidService {

	@Override
	public Bid placeNewBid(String userId, Integer roundNumber, Double tokenAmount, Double tokenPrice) {
		// create new pending bid
		Bid bid = new Bid(userId, roundNumber, tokenAmount, tokenPrice);
		bidDao.placeBid(bid);
		return bid;
	}

	@Override
	public List<Bid> getBidListByRound(Integer round) {
		return bidDao.getBidListByRound(round);
	}

	@Override
	public List<Bid> getBidListByUser(String userId) {
		return bidDao.getBidListByUser(userId);
	}

	@Override
	public Bid getBid(Integer round, String userId) {
		return bidDao.getBid(round, userId);
	}

	@Override
	public Bid updateBid(String userId, Integer roundNumber, Double tokenAmount, Double tokenPrice) {
		Bid bid = bidDao.getBid(roundNumber, userId);
		bid.setTokenAmount(tokenAmount);
		bid.setTokenPrice(tokenPrice);
		bid.setTotalPrice(tokenPrice * tokenAmount);
		return bidDao.updateBid(bid);
	}

	/**
	 * It is called from Payment service with user id and round number.
	 */
	@Override
	public void updateBidRanking(String userId, Integer roundNumber) {
		Bid bid = bidDao.getBid(roundNumber, userId);
		List<Bid> bidList = bidDao.getBidListByRound(roundNumber);
		bidList.add(bid);
		
	}

}
