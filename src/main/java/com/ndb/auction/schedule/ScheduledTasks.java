package com.ndb.auction.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.payment.WithdrawService;
import com.ndb.auction.web3.NDBCoinService;

@Component
public class ScheduledTasks {

	@Autowired
	AuctionService auctionService;

	@Autowired
	BidService bidService;

	@Autowired
	StatService statService;

	@Autowired
	PresaleService presaleService;

	@Autowired
	WithdrawService withdrawService;

	@Autowired
	NDBCoinService ndbCoinService;

	private Auction startedRound;
	private Long startedCounter;

	private Auction readyRound;
	private Long readyCounter;

	private PreSale startedPresale;
	private Long startedPresaleCounter;

	private PreSale readyPresale;
	private Long readyPresaleCounter;

	// check transaction
	private Map<String, BigInteger> pendingTransactions;

	public ScheduledTasks() {
		this.readyCounter = 0L;
		this.startedCounter = 0L;
		this.startedRound = null;
		this.readyRound = null;

		this.startedPresale = null;
		this.startedPresaleCounter = 0l;
		this.readyPresale = null;
		this.readyPresaleCounter = 0l;

		pendingTransactions = new HashMap<>();
	}

	public void checkAllRounds() {
		Long currentTime = System.currentTimeMillis();
		
		// check Auctions
		List<Auction> auctions = auctionService.getAuctionByStatus(Auction.COUNTDOWN);
		if(auctions.size() != 0) {
			Auction auction = auctions.get(0);
			if(auction.getStartedAt() > currentTime) {
				// start count down
				setNewCountdown(auction);
				System.out.println(String.format("Auction Round %d is in countdown.", auction.getRound()));
				return;
			} else if (auction.getStartedAt() < currentTime && auction.getEndedAt() > currentTime) {
				// start round
				setStartRound(auction);
				auctionService.startAuction(auction.getId());
				System.out.println(String.format("Auction Round %d has been started.", auction.getRound()));
				return;
			} else {
				auctionService.endAuction(auction.getId());
				return;
			}
		}
		
		auctions = auctionService.getAuctionByStatus(Auction.STARTED);
		if(auctions.size() != 0) {
			Auction auction = auctions.get(0);
			if(auction.getStartedAt() < currentTime && auction.getEndedAt() > currentTime) {
				// start round
				setStartRound(auction);
				System.out.println(String.format("Auction Round %d has been started.", auction.getRound()));
				return;
			} else {
				auctionService.endAuction(auction.getId());
				return;
			}
		}

		List<PreSale> presales = presaleService.getPresaleByStatus(PreSale.COUNTDOWN);
		if(presales.size() != 0) {
			PreSale presale = presales.get(0);
			if(presale.getStartedAt() > currentTime) {
				setPresaleCountdown(presale);
				System.out.println(String.format("PreSale Round %d is in countdown.", presale.getId()));
				return;
			} else if (presale.getStartedAt() < currentTime && presale.getEndedAt() > currentTime) {
				setPresaleStart(presale);
				presaleService.startPresale(presale.getId());
				System.out.println(String.format("PreSale Round %d has been started.", presale.getId()));
				return;
			} else {
				presaleService.closePresale(presale.getId());
				return;
			}
		}

		presales = presaleService.getPresaleByStatus(PreSale.STARTED);
		if(presales.size() != 0) {
			PreSale presale = presales.get(0);
			if(presale.getStartedAt() < currentTime && presale.getEndedAt() > currentTime) {
				setPresaleStart(presale);
				System.out.println(String.format("PreSale Round %d has been started.", presale.getId()));
				return;
			} else {
				presaleService.closePresale(presale.getId());
				return;
			}
		}
	}

	public Integer setNewCountdown(Auction auction) {

		if (this.readyRound != null) {
			return -1;
		}

		this.readyRound = auction;
		this.readyCounter = auction.getStartedAt() - System.currentTimeMillis();
		// convert into Seconds!!
		this.readyCounter /= 1000;

		return 1;
	}

	public void setStartRound(Auction auction) {
		if (this.startedRound != null) {
			return;
		}
		this.startedRound = auction;
		this.startedCounter = auction.getEndedAt() - System.currentTimeMillis();
		this.startedCounter /= 1000;
	}

	public void setPresaleCountdown(PreSale presale) {
		this.readyPresale = presale;
		this.readyPresaleCounter = presale.getStartedAt() - System.currentTimeMillis();
		this.readyPresaleCounter /= 1000;
	}

	public void setPresaleStart(PreSale presale) {
		this.startedPresale = presale;
		this.startedPresaleCounter = presale.getStartedAt() - System.currentTimeMillis();
		this.startedPresaleCounter /= 1000;
	}

	@Scheduled(fixedRate = 1000)
	public void AuctionCounter() {

		// count down ( ready round )
		if (readyRound != null && readyCounter > 0L) {
			readyCounter--;
			System.out.println("Ready counter: " + readyCounter.toString());
			if (readyCounter <= 0) {
				// ended count down ! trigger to start this round!!

				startedRound = readyRound;
				startedCounter = (readyRound.getEndedAt() - readyRound.getStartedAt()) / 1000;

				int id = readyRound.getId();
				auctionService.startAuction(id);
			}
		}

		// check current started round
		if (startedRound != null && startedCounter > 0L) {
			startedCounter--;
			System.out.println("Started counter: " + startedCounter.toString());
			if (startedCounter <= 0) {
				// end round!
				auctionService.endAuction(startedRound.getId());
				
				// bid processing
				// ********* checking delayed more 1s ************
				bidService.closeBid(startedRound.getRound());
				statService.updateRoundCache(startedRound.getRound());
				startedRound = null;
			}
		}
	
		if (readyPresaleCounter > 0L && readyPresale != null) {
			readyPresaleCounter--;
			System.out.println("Ready counter: " + readyPresaleCounter.toString());
			if(readyPresaleCounter == 0L) {
				startedPresale = readyPresale;
				startedPresaleCounter = (readyPresale.getEndedAt() - readyPresale.getStartedAt()) / 1000;
				presaleService.startPresale(readyPresale.getId());
				readyPresale = null;
				
				System.out.println("Started counter: " + startedPresaleCounter.toString());
			}
		}

		if (startedPresale != null && startedPresaleCounter > 0L) {
			startedPresaleCounter--;
			if(startedPresaleCounter == 0) {
				// end
				presaleService.closePresale(startedPresale.getId());
				startedPresale = null;
				System.out.println("Presale Ended: " + startedPresaleCounter.toString());
			}
		}
		
	}

	// add pending list
	public void addPendingTxn(String hash, BigInteger blockNum) {
		if(pendingTransactions.containsKey(hash)) 
			return;
		pendingTransactions.put(hash, blockNum);
	}

	@Scheduled(fixedRate = 1000 * 120)
	public void checkConfirmation() {
		Set<String> hashSet = this.pendingTransactions.keySet();
		for (String hash : hashSet) {
			BigInteger target = this.pendingTransactions.get(hash);
			if(ndbCoinService.checkConfirmation(target)) {
				// set success
				System.out.println("SUCCESS: " + hash);
				withdrawService.updateStatus(hash);
				pendingTransactions.remove(hash);
			}
		}
	}

}
