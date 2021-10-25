package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Auction;

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
		
		
		return null;
	}

	@Override
	public Auction endAuction(String id) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
