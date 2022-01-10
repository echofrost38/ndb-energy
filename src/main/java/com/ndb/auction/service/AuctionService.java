package com.ndb.auction.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.service.user.UserDetailsImpl;

@Service
@RequiredArgsConstructor
public class AuctionService extends BaseService {

	public Auction createNewAuction(Auction auction) {

		// Started at checking
		if (System.currentTimeMillis() > auction.getStartDate()) {
			throw new AuctionException("Round start time is invalid.", String.valueOf(auction.getId()));
		}

		// check conflict auction round
		Auction _auction = auctionDao.getAuctionByRound(auction.getRound());
		if (_auction != null) {
			throw new AuctionException("Round doesn't exist.", String.valueOf(auction.getId()));
		}

		Auction prev = auctionDao.getAuctionByRound(auction.getRound() - 1);
		if (prev == null || prev.getStatus() == Auction.STARTED) {
			auction.setStatus(Auction.COUNTDOWN);

			// Save
			auctionDao.createNewAuction(auction);
			auctionAvatarDao.update(auction.getId(), auction.getAvatar());

			// set new countdown!!
			schedule.setNewCountdown(auction);

		} else {
			// check end time and start time
			long prevEnd = prev.getEndDate();
			long curStart = auction.getStartDate();
			if (curStart < prevEnd)
				throw new AuctionException("Round start time is invalid.", String.valueOf(auction.getId()));

			auctionDao.createNewAuction(auction);
			auctionAvatarDao.update(auction.getId(), auction.getAvatar());
		}

		return auction;
	}

	public List<Auction> getAuctionList() {
		return auctionDao.getAuctionList();
	}

	public Auction getAuctionById(int id) {
		return auctionDao.getAuctionById(id);
	}

	public Auction getAuctionByRound(int round) {
		return auctionDao.getAuctionByRound(round);
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

	public Auction startAuction(int id) {

		// check already opened Round
		List<Auction> list = auctionDao.getAuctionByStatus(Auction.STARTED);
		if (list.size() != 0) {
			// there is already opened auction
			return null; // or exception
		}

		// check current auction is pending
		Auction target = auctionDao.getAuctionById(id);
		if (target.getStatus() != Auction.COUNTDOWN) {
			// it isn't PENDING round
			return null; // or exception
		}

		auctionDao.startAuction(target);

		// get next round
		Auction nextRound = auctionDao.getAuctionByRound(target.getStatus() + 1);
		if (nextRound != null) {
			nextRound.setStatus(Auction.COUNTDOWN);
			auctionDao.updateAuctionStats(nextRound);
		}
		// send notification
		System.out.println("Auction Started, Please send me as Notification!");

		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		notificationService.sendNotification(
				userDetails.getId(),
				Notification.N_AUCTION_START,
				"Auction Started",
				"Auction Started please bid!");

		return nextRound;
	}

	public Auction endAuction(int id) {
		// check Auction is Started!
		Auction target = auctionDao.getAuctionById(id);
		if (target.getStatus() != Auction.STARTED) {
			return null; // or exception
		}
		auctionDao.endAuction(target);

		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		notificationService.sendNotification(
				userDetails.getId(),
				Notification.N_AUCTION_END,
				"Auction Finished",
				"Please check you bid results");

		return target;
	}

	public List<Auction> getAuctionByStatus(Integer status) {
		return auctionDao.getAuctionByStatus(status);
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

}
