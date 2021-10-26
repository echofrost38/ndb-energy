package com.ndb.auction.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;
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
		Bid[] bidList = bidDao.getBidListByRound(round).toArray(new Bid[0]);
		Arrays.sort(bidList, Comparator.comparingDouble(Bid::getTokenPrice).reversed());
		return Arrays.asList(bidList);
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
		
		Auction currentRound = auctionDao.getAuctionByRound(roundNumber);
		
		Bid bid = bidDao.getBid(roundNumber, userId);
		List<Bid> bidList = bidDao.getBidListByRound(roundNumber);
		bidList.add(bid);
		Bid[] newList = bidList.toArray(new Bid[0]);
		Arrays.sort(newList, Comparator.comparingDouble(Bid::getTokenPrice).reversed());
		
		// true : winner, false : fail
		boolean status = true; 
		
		// qty, win, fail : total price ( USD )
		double qty = 0.0, win = 0.0, fail = 0.0;
		double availableToken = currentRound.getTotalToken();
		
		int len = newList.length;
		for(int i = 0; i < len; i++) {
			if(status) {
				win += newList[i].getTokenPrice();
				newList[i].setStatus(Bid.WINNER);
			} else {
				fail += newList[i].getTokenPrice();
				newList[i].setStatus(Bid.FAILED);
			}
			availableToken -= bid.getTokenAmount();
			
			if(availableToken < 0) {
				status = false; // change to fail
				win -= availableToken;
				fail += availableToken;
			}   
		}
		
		// Save new Bid status
		bidDao.updateBidStatus(Arrays.asList(newList));
		
        //update & save new auction stats
		currentRound.setStats(new AuctionStats(qty, win, fail));       
        auctionDao.updateAuctionStats(currentRound);
        
        // Call notify !!!!!
	}

}
