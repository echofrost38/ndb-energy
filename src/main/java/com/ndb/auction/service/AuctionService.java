package com.ndb.auction.service;

import java.util.List;

import javax.annotation.PostConstruct;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.presale.PreSale;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService extends BaseService {

	@PostConstruct
	public void init() {
		schedule.checkAllRounds();
	}

	public Auction createNewAuction(Auction auction) {

		// Started at checking
		if (System.currentTimeMillis() > auction.getStartedAt()) {
			System.out.println(System.currentTimeMillis());
			throw new AuctionException("Round start time is invalid.", String.valueOf(auction.getId()));
		}

		// check conflict auction round
		Auction _auction = auctionDao.getAuctionByRound(auction.getRound());
		if (_auction != null) {
			throw new AuctionException("Round already exist.", String.valueOf(auction.getId()));
		}

		// started round
		List<Auction> auctions = auctionDao.getAuctionByStatus(Auction.COUNTDOWN);
		if(auctions.size() != 0) {
			throw new AuctionException("countdown_auction", String.valueOf(auction.getId()));
		}		
		auctions = auctionDao.getAuctionByStatus(Auction.STARTED);
		if(auctions.size() != 0) {
			throw new AuctionException("started_auction", String.valueOf(auction.getId()));
		}		

		// check presale
		List<PreSale> presales = presaleDao.selectByStatus(PreSale.COUNTDOWN);
		if(presales.size() != 0) {
			throw new AuctionException("countdown_presale", String.valueOf(auction.getId()));
		}
		presales = presaleDao.selectByStatus(PreSale.STARTED);
		if(presales.size() != 0) {
			throw new AuctionException("started_presale", String.valueOf(auction.getId()));
		}

		auction = auctionDao.createNewAuction(auction);
		// add auction avatar
		for (AvatarSet avatarSet : auction.getAvatar()) {
			avatarSet.setId(auction.getId());
			auctionAvatarDao.insert(avatarSet);
		}
		schedule.setNewCountdown(auction);
		return auction;
	}

	public List<Auction> getAuctionList() {
		List<Auction> auctionList = auctionDao.getAuctionList();
		for (Auction auction : auctionList) {
			auction.setAvatar(auctionAvatarDao.selectById(auction.getId()));
		}
		return auctionList;
	}

	public Auction getAuctionById(int id) {
		Auction auction = auctionDao.getAuctionById(id);
		if(auction == null) return null;
		auction.setAvatar(auctionAvatarDao.selectById(auction.getId()));
		return auction;
	}

	public Auction getAuctionByRound(int round) {
		Auction auction = auctionDao.getAuctionByRound(round);
		if(auction == null) return null;
		auction.setAvatar(auctionAvatarDao.selectById(auction.getId()));
		return auction;
	}

	public Auction updateAuctionByAdmin(Auction auction) {

		// Check Validation ( null possible )
		Auction _auction = auctionDao.getAuctionById(auction.getId());
		if (_auction == null)
			return null;
		if (_auction.getStatus() != Auction.PENDING)
			throw new AuctionException("Round is not pending status.", String.valueOf(auction.getId()));

		auctionDao.updateAuctionByAdmin(auction);
		auctionAvatarDao.update(auction.getId(), auction.getAvatar());
		return auction;
	}

	public void startAuction(int id) {

		// check already opened Round
		List<Auction> list = auctionDao.getAuctionByStatus(Auction.STARTED);
		if (list.size() != 0) {
			throw new AuctionException("There is already opened round.", "id");
		}

		// check current auction is pending
		Auction target = auctionDao.getAuctionById(id);
		if (target.getStatus() != Auction.COUNTDOWN) {
			throw new AuctionException("It is not a pending round.", "id");
		}

		auctionDao.startAuction(target);

		notificationService.broadcastNotification(
			Notification.NEW_ROUND_STARTED, 
			"NEW ROUND STARTED", 
			"Auction Round " + target.getRound() + " has been started.");

	}

	public Auction endAuction(int id) {
		// check Auction is Started!
		Auction target = auctionDao.getAuctionById(id);
		if (target.getStatus() != Auction.STARTED) {
			return null; // or exception
		}
		auctionDao.endAuction(target);

		String msg = String.format("ROUND %d FINISHED.", target.getRound());
		String title = "ROUND FINISHED";
		notificationService.broadcastNotification(Notification.ROUND_FINISHED, title, msg);

		return target;
	}

	public List<Auction> getAuctionByStatus(Integer status) {
		List<Auction> auctionList = auctionDao.getAuctionByStatus(status);
		for (Auction auction : auctionList) {
			auction.setAvatar(auctionAvatarDao.selectById(auction.getId()));
		}
		return auctionList;
	}

	public String checkRounds() {
		List<Auction> auctions = auctionDao.getAuctionByStatus(Auction.COUNTDOWN);
		if (auctions.size() != 0) {
			Auction auction = auctions.get(0);
			schedule.setNewCountdown(auction);
		}

		auctions = auctionDao.getAuctionByStatus(Auction.STARTED);
		if (auctions.size() != 0) {
			Auction auction = auctions.get(0);
			schedule.setStartRound(auction);
		}
		return "Checked";
	}

	public int getNewRound() {
		return auctionDao.getNewRound();
	}

}
