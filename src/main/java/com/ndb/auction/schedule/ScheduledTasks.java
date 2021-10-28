package com.ndb.auction.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;
import com.ndb.auction.service.AuctionService;

@Component
public class ScheduledTasks {
	
	@Autowired
	AuctionService auctionService;
	
	private Auction openedRound;
	private Long counter;
	
	public ScheduledTasks() {
		this.openedRound = null;
		this.counter = 0L;
	}
	
	public Auction getOpenedRound() {
		return openedRound;
	}
	public void setOpenedRound(Auction openedRound) {
		this.openedRound = openedRound;
	}
	
	public Long getCounter() {
		return counter;
	}
	public void setCounter(Long counter) {
		this.counter = counter;
	}

	@Scheduled(fixedRate = 1000)
	public void AuctionCounter() {		
		if(openedRound == null) {
			return;
		}
		
		counter--;
		if(counter == 0) {
			auctionService.endAuction(openedRound.getAuctionId());
			openedRound = null;
		}		
	}
}
