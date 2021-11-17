package com.ndb.auction.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.BidService;

@Component
public class ScheduledTasks {
	
	@Autowired
	AuctionService auctionService;

	@Autowired
	BidService bidService;
	
	private Auction startedRound;
	private Long startedCounter;

	private Auction readyRound;
	private Long readyCounter;
	
	public AuctionService getAuctionService() {
		return auctionService;
	}

	public void setAuctionService(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	public Auction getStartedRound() {
		return startedRound;
	}

	public void setStartedRound(Auction startedRound) {
		this.startedRound = startedRound;
	}

	public Long getStartedCounter() {
		return startedCounter;
	}

	public void setStartedCounter(Long startedCounter) {
		this.startedCounter = startedCounter;
	}

	public Auction getReadyRound() {
		return readyRound;
	}

	public void setReadyRound(Auction readyRound) {
		this.readyRound = readyRound;
	}

	public Long getReadyCounter() {
		return readyCounter;
	}

	public void setReadyCounter(Long readyCounter) {
		this.readyCounter = readyCounter;
	}

	public ScheduledTasks() {
		this.readyCounter = 0L;
		this.startedCounter = 0L;
		this.startedRound = null;
		this.readyRound = null;
	}

	public Integer setNewCountdown(Auction auction) {
		
		if(this.readyRound != null) {
			return -1;
		}
		
		this.readyRound = auction;
		this.readyCounter = auction.getStartedAt() - System.currentTimeMillis();
		// convert into Seconds!!
		this.readyCounter /= 1000; 
		
		return 1;
	}

	public void setStartRound(Auction auction) {
		if(this.startedRound != null) {
			return;
		}
		this.startedRound = auction;
		this.startedCounter = auction.getEndedAt() - System.currentTimeMillis();
		this.startedCounter /= 1000;
	}

	@Scheduled(fixedRate = 1000)
	public void AuctionCounter() {
		
		// count down ( ready round )
		if(readyRound != null && readyCounter > 0L) {
			readyCounter--;
			System.out.println("Ready counter: " + readyCounter.toString());
			if(readyCounter == 0) {
				// ended count down ! trigger to start this round!!
				
				startedRound = readyRound;
				startedCounter = readyRound.getDuration();
				
				String id = readyRound.getAuctionId();
				Auction nextRound = auctionService.startAuction(id);
				if(nextRound != null) {
					readyRound = nextRound;
					readyCounter = (nextRound.getStartedAt() - System.currentTimeMillis()) / 1000;
				} else {
					readyRound = null;
				}
			}
		}

		// check current started round
		if(startedRound != null && startedCounter > 0L) {
			startedCounter--;
			System.out.println("Started counter: " + startedCounter.toString());
			if(startedCounter == 0) {
				// end round!
				auctionService.endAuction(startedRound.getAuctionId());
				
				startedRound = null;
				
				// bid processing 
				// ********* checking delayed more 1s ************ 
				bidService.closeBid(startedRound.getAuctionId());
			}
		}
	}
}
