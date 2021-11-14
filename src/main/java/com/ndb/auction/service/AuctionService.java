package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Auction;
import com.ndb.auction.service.interfaces.IAuctionService;

@Service
public class AuctionService extends BaseService implements IAuctionService {

	@Override
	public Auction createNewAuction(Auction auction) {
		
		// check conflict auction round
		Auction _auction = auctionDao.getAuctionByRound(auction.getNumber());
		if(_auction != null) {
			return null;
		}

		Auction prev = auctionDao.getAuctionByRound(auction.getNumber() - 1);
		if(prev == null || prev.getStatus() == Auction.STARTED) {
			auction.setStatus(Auction.COUNTDOWN);

			// Save
			auctionDao.createNewAuction(auction);

			// set new countdown!!
			schedule.setNewCountdown(auction);

		} else {
			// check end time and start time
			long prevEnd = prev.getEndedAt();
			long curStart = auction.getStartedAt();
			if(curStart < prevEnd) return null;

			auctionDao.createNewAuction(auction);
		}
		
		return auction;
	}

	@Override
	public List<Auction> getAuctionList() {
		return auctionDao.getAuctionList();
	}

	@Override
	public Auction getAuctionById(String id) {
		return auctionDao.getAuctionById(id);
	}

	@Override
	public Auction updateAuctionByAdmin(Auction auction) {
				
		// Check Validation ( null possible ) 
		Auction _auction = auctionDao.getAuctionById(auction.getAuctionId());
		if(_auction == null) return null;
		if(_auction.getStatus() != Auction.PENDING) return null;

		return auctionDao.updateAuctionByAdmin(auction);
	}

	@Override
	public Auction startAuction(String id) {
		
		// check already opened Round
		List<Auction> list = auctionDao.getAuctionByStatus(Auction.STARTED);
		if(list.size() != 0) {
			// there is already opened auction
			return null; // or exception
		}
		
		// check current auction is pending
		Auction target = auctionDao.getAuctionById(id);
		if(target.getStatus() != Auction.COUNTDOWN) {
			// it isn't PENDING round
			return null; // or exception
		}

		auctionDao.startAuction(target);

		// get next round
		Auction nextRound = auctionDao.getAuctionByRound(target.getStatus() + 1);
		if(nextRound != null) {
			nextRound.setStatus(Auction.COUNTDOWN);
			auctionDao.updateAuctionStats(nextRound);
		}
		return nextRound;
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
