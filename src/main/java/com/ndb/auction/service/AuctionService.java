package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Auction;
import com.ndb.auction.service.interfaces.IAuctionService;

@Service
public class AuctionService extends BaseService implements IAuctionService {

	@Override
	public Auction createNewAuction(Auction auction) {
		
		// check Admin role  
		
		// check duplicate number
		
		// Validation
		
		// Save
		auctionDao.createNewAuction(auction);
		
		return auction;
	}

	@Override
	public List<Auction> getAuctionList() {
		
		// Check Client or Admin Role
		
		return auctionDao.getAuctionList();
	}

	@Override
	public Auction getAuctionById(String id) {
		
		// Check Client or Admin Role
		
		return auctionDao.getAuctionById(id);
	}

	@Override
	public Auction updateAuctionByAdmin(Auction auction) {
		
		// Check Admin Role
		
		// Check Validation ( null possible ) 
		
		return auctionDao.updateAuctionByAdmin(auction);
	}

	@Override
	public Auction startAuction(String id) {
		
		// check already opened Round
		List<Auction> list = auctionDao.getOpendedList();
		if(list.size() != 0) {
			// there is already opened auction
			return null; // or exception
		}
		
		// check current auction is pending
		Auction target = auctionDao.getAuctionById(id);
		if(target.getStatus() != Auction.PENDING) {
			// it isn't PENDING round
			return null; // or exception
		}
		
		return auctionDao.startAuction(target);
	}

	@Override
	public Auction endAuction(String id) {
		// check Auction is Started!
		Auction target = auctionDao.getAuctionById(id);
		if(target.getStatus() != Auction.STARTED) {
			return null; // or exception
		}
		auctionDao.endAuction(target);
		return target;
	}
    
}
