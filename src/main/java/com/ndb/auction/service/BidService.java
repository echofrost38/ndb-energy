package com.ndb.auction.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;
import com.ndb.auction.models.Bid;

/**
 * TODOs
 * 1. UpdateBid Logic 
 * 2. Bid payment status
 * 3. Notification
 * @author klinux
 *
 */

@Service
public class BidService extends BaseService implements IBidService {

	@Override
	public Bid placeNewBid(
			String userId, 
			Integer roundNumber, 
			Double tokenAmount, 
			Double tokenPrice,
			String payType
	) {
		// create new pending bid
		Bid bid = new Bid(userId, roundNumber, tokenAmount, tokenPrice);
		
		// check Round is opened. 
		Auction auction = auctionDao.getAuctionByRound(roundNumber);
		if(auction.getStatus() != Auction.STARTED) {
			return null; // or exception
		}
		
		// set bid type
		bid.setPayType(payType);

		// save with pending status
		bidDao.placeBid(bid);
		return bid;
	}

	@Override
	public List<Bid> getBidListByRound(Integer round) {
		// PaginatedScanList<> how to sort?
		Bid[] bidList = bidDao.getBidListByRound(round).toArray(new Bid[0]);
		Arrays.sort(bidList, Comparator.comparingDouble(Bid::getTokenPrice).reversed());
		return Arrays.asList(bidList);
	}

	@Override
	public List<Bid> getBidListByUser(String userId) {
		// User's bidding history
		return bidDao.getBidListByUser(userId);
	}

	@Override
	public Bid getBid(Integer round, String userId) {
		return bidDao.getBid(round, userId);
	}

	@Override
	public Bid updateBid(String userId, Integer roundNumber, Double tokenAmount, Double tokenPrice) {
		
		Bid bid = bidDao.getBid(roundNumber, userId);
		
		// check null 
		if(bid == null) {
			return null; // or exception
		}
		
		// 
		
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
		
        // update & save new auction stats
		currentRound.setStats(new AuctionStats(qty, win, fail));       
        auctionDao.updateAuctionStats(currentRound);
        
        // Call notify !!!!!
        
	}
	
	public void closeBid(Integer round) {
		// Assume all status already confirmed when new bid is placed
		List<Bid> bidList = bidDao.getBidListByRound(round);
		
		// processing all bids
		ListIterator<Bid> iterator = bidList.listIterator();
	    while (iterator.hasNext()) {
	        Bid bid = iterator.next();
	        
	        String userId = bid.getUserId();
	        
	        switch (bid.getPayType()) {
		        case "CREDIT":
		        	// get Fiat payment transaction
		        	
		        	if(bid.getStatus() == Bid.WINNER) {
		        		// capture payment
		        		
		        		// get capture result
		        		
		        		// if success ALLOC NDB
		        	} else if (bid.getStatus() == Bid.FAILED) {
		        		// cancel authorization
		        		
		        	}
		        	break;
		        case "CRYPTO":
		        	// get Crypto payment transaction
		        	
		        	if(bid.getStatus() == Bid.WINNER) {
		        		// holding -> deduct
		        		
		        		// Alloc NDB
		        		
		        	} else if (bid.getStatus() == Bid.FAILED) {
		        		// holding -> release in user's wallet
		        		
		        	}
		        	break;
		        case "WALLET":
		        	if(bid.getStatus() == Bid.WINNER) {
		        		// holding -> deduct
		        		
		        		// Alloc NDB
		        		
		        	} else if (bid.getStatus() == Bid.FAILED) {
		        		// holding -> release in user's wallet
		        		
		        	}
		        	break;
		        default:
		        	break;
	        }
	        
	        // call payment dao!
	        bid.getTotalPrice();
	    }
	}

}
