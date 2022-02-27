package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentAuctionService;
import com.ndb.auction.service.payment.stripe.StripeAuctionService;
import com.ndb.auction.utils.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODOs
 * 1. UpdateBid Logic
 * 2. Bid payment status
 * 3. Notification
 * 
 * @author klinux
 *
 */

@Service
public class BidService extends BaseService {

	@Autowired
	private Sort sort;

	@Autowired
	private StripeAuctionService stripeService;

	@Autowired
	private CoinpaymentAuctionService coinpaymentAuctionService;

	// cache bid list for ongoing auction round
	private List<Bid> currentBidList;

	public BidService() {
		this.currentBidList = null;
	}

	// fill bid list
	private synchronized void fillBidList(int roundId) {
		if ( currentBidList == null ) {
			currentBidList = new ArrayList<>();
		} else {
			currentBidList.clear();
		}
		currentBidList = bidDao.getBidListByRound(roundId);

		// set Ranking!
		if(currentBidList.size() == 0) return;

		Bid bids[] = new Bid[currentBidList.size()];
		currentBidList.toArray(bids);
		sort.mergeSort(bids, 0, bids.length - 1);

		// set ranking
		currentBidList.clear();
		int len = bids.length;
		for (int i = 0; i < len; i++) {
			Bid bid = bids[i];
			bid.setRanking(i + 1);
			currentBidList.add(bid);
		}
	}
 
	public Bid placeNewBid(
			int userId,
			int roundId,
			long tokenAmount,
			long tokenPrice) {
		// Check existing
		Bid bid = bidDao.getBid(userId, roundId);
		if (bid != null && bid.getStatus() != Bid.NOT_CONFIRMED) {
			throw new BidException("Already place a bid to this round.", "roundId");
		}

		// create new pending bid
		// double totalPrice = Double.valueOf(tokenAmount * tokenPrice);
		if(bid == null) {
			bid = new Bid(userId, roundId, tokenAmount, tokenPrice);
		} else {
			bid.setUserId(userId);
			bid.setRoundId(roundId);
			bid.setTokenAmount(tokenAmount);
			bid.setTokenPrice(tokenPrice);
		}

		// check Round is opened.
		Auction auction = auctionDao.getAuctionById(roundId);

		if(auction == null) {
			throw new AuctionException("There is not that round.", "roundId");
		}

		if (auction.getStatus() != Auction.STARTED) {
			throw new BidException("Round is not yet started.", "roundId");
		}

		if (auction.getMinPrice() > tokenPrice) {
			throw new BidException("Token price must be larget than min price.", "tokenPrice");
		}

		// check pay type : WALLET!!!!!
		// if (payType == Bid.WALLET) {
		// 	// check user's wallet!
		// 	double cryptoAmount = totalPrice / cryptoService.getCryptoPriceBySymbol(cryptoType);

		// 	int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
		// 	CryptoBalance balance = balanceDao.selectById(userId, tokenId);
			
		// 	if (balance.getFree() < cryptoAmount) {
		// 		throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
		// 	}

		// 	balanceDao.makeHoldBalance(userId, tokenId, cryptoAmount);

		// 	Map<String, BidHolding> holdingList = bid.getHoldingList();
		// 	BidHolding hold = new BidHolding(cryptoAmount, totalPrice);
		// 	holdingList.put(cryptoType, hold);
		// 	bid.setHoldingList(holdingList);

		// 	bidDao.placeBid(bid);
		// 	updateBidRanking(bid);
		// 	return bid;
		// }

		// save with pending status
		bidDao.placeBid(bid);
		return bid;
	}

	private void addAuctionpoint(int userId, int roundNumber) {
		TierTask tierTask = tierTaskService.getTierTask(userId);
		TaskSetting taskSetting = taskSettingService.getTaskSetting();
		List<Tier> tiers = tierService.getUserTiers();

		if(tierTask == null) {
			tierTask = new TierTask(userId);
			tierTaskService.updateTierTask(tierTask);
		}

		if (tierTask.getAuctions().contains(roundNumber)) {
			return;
		}
		User user = userDao.selectById(userId);
		tierTask.getAuctions().add(roundNumber);
		double point = user.getTierPoint();
		point += taskSetting.getAuction();
		double _point = point;
		int level = user.getTierLevel();
		for (Tier tier : tiers) {
			if (tier.getPoint() >= point && tier.getPoint() > _point) {
				_point = tier.getPoint();
				level = tier.getLevel();
			}
		}
		tierTaskService.updateTierTask(tierTask); // TODO: why update?
		userDao.updateTier(userId, level, point);
	}

	public List<Bid> getBidListByRound(int round) {
		// PaginatedScanList<> how to sort?
		Auction auction = auctionDao.getAuctionByRound(round);
		if(auction == null) {
			throw new AuctionException("There is no round.", "round");
		}

		if(auction.getStatus() == Auction.STARTED) {
			if(currentBidList == null) fillBidList(auction.getId());
			return currentBidList;
		}
		return bidDao.getBidListByRound(auction.getId());
	}

	public List<Bid> getBidListByRoundId(int round) {
		// check round status
		Auction currentRound = auctionDao.getAuctionById(round);
		if(currentRound == null) {
			throw new AuctionException("There is no round.", "round");
		}

		if(currentRound.getStatus() == Auction.STARTED) {
			if(currentBidList == null) fillBidList(round);
			return currentBidList;
		}

		return bidDao.getBidListByRound(round);
	}

	public List<Bid> getBidListByUser(int userId) {
		// User's bidding history
		return bidDao.getBidListByUser(userId);
	}

	public Bid getBid(int roundId, int userId) {
		return bidDao.getBid(userId, roundId);
	}

	/**
	 * It is called from Payment service with user id and round number.
	 */
	public void updateBidRanking(Bid bid) {
		int roundId = bid.getRoundId();
		int userId = bid.getUserId();
		Auction currentRound = auctionDao.getAuctionById(roundId);

		// check round status
		if(currentRound.getStatus() != Auction.STARTED) {
			return;
		}

		// assume winner!
		addAuctionpoint(userId, currentRound.getRound());
		
		if(currentBidList == null) fillBidList(roundId);
		
		// checking already exists
		boolean exists = false;
		for (Bid _bid : currentBidList) {
			if(_bid.getUserId() == userId && _bid.getRoundId() == roundId) {
				currentBidList.remove(_bid);
				currentBidList.add(bid);
				exists = true;
			}
		}
		if(!exists) {
			currentBidList.add(bid);
		}

		bidDao.updateStatus(userId, roundId, 1);
		
		Bid newList[] = new Bid[currentBidList.size()];
		currentBidList.toArray(newList);

		// Sort Bid List by
		// 1. Token Price
		// 2. Total Price ( Amount of pay )
		// 3. Placed time ( early is winner )
		sort.mergeSort(newList, 0, newList.length - 1);

		// true : winner, false : fail
		boolean status = true;

		// qty, win, fail : total price ( USD )
		long win = 0, fail = 0;
		final long total = currentRound.getTotalToken();
		long availableToken = total;

		int len = newList.length;
		for (int i = 0; i < len; i++) {
			boolean statusChanged = false;
			Bid tempBid = newList[i];
			
			// Rank changed
			if(tempBid.getRanking() != (i+1)) {
				statusChanged = true;
				tempBid.setRanking(i+1);
			}

			// status changed Winner or failer
			if (status) {
				win += tempBid.getTokenAmount();
				if(tempBid.getStatus() != Bid.WINNER) {
					tempBid.setStatus(Bid.WINNER);
					statusChanged = true;
				}
			} else {
				fail += tempBid.getTokenAmount();
				if(tempBid.getStatus() != Bid.FAILED) {
					tempBid.setStatus(Bid.FAILED);
					statusChanged = true;
				}
			}
			availableToken -= tempBid.getTokenAmount();

			if (availableToken < 0) {
				status = false; // change to fail
				win -= availableToken;
				fail += availableToken;
			}

			if(statusChanged) {
				// bidDao.updateStatus(tempBid.getUserId(), tempBid.getRoundId(), tempBid.getStatus());
	
				// send Notification
				notificationService.sendNotification(
					tempBid.getUserId(),
					Notification.BID_RANKING_UPDATED,
					"BID RANKING UPDATED",
					String.format("Bid ranking is updated into %d, your bid is %s.", 
						i + 1, tempBid.getStatus() == Bid.WINNER ? "WINNER" : "FAILED")
				);
			}
			
		}

		// update & save new auction stats
		currentRound.setStats(new AuctionStats(win + fail, win, fail));
		if (win > total) {
			currentRound.setSold(total);
		} else {
			currentRound.setSold(win);
		}
		auctionDao.updateAuctionStats(currentRound);
	}

	// not sychnorized
	public void closeBid(int roundId) {

		Auction auction = auctionDao.getAuctionByRound(roundId);
		List<AvatarSet> avatar = auctionAvatarDao.selectById(auction.getId());
		List<AvatarComponent> avatarComponents = avatarComponentDao.getAvatarComponentsBySet(avatar);
		double avatarToken = auction.getToken();
		double totalToken = auction.getTotalToken();

		// processing all bids
		if(currentBidList == null) fillBidList(roundId);
		ListIterator<Bid> iterator = currentBidList.listIterator();
		while (iterator.hasNext()) {

			Bid bid = iterator.next();

			int userId = bid.getUserId();
			long totalPrice = 0;

			// check stripe
			List<StripeAuctionTransaction> fiatTxns = stripeService.selectByIds(roundId, userId);
			for (StripeAuctionTransaction fiatTransaction : fiatTxns) {

				// Processing Stripe CAPTURE or CANCEL
				boolean result = stripeService.UpdateTransaction(fiatTransaction.getId(), bid.getStatus());
				if (result && (bid.getStatus() == Bid.WINNER)) {
					totalPrice += fiatTransaction.getAmount();
				}
			}

			List<CoinpaymentAuctionTransaction> cryptoTxns = 
				(List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
			for (CoinpaymentAuctionTransaction cryptoTransaction : cryptoTxns) {
				if (!cryptoTransaction.getStatus()) {
					continue;
				}
				if (bid.getStatus() == Bid.WINNER) {
					String sAmount = cryptoTransaction.getAmount().toString();
					totalPrice += Long.valueOf(sAmount);

					// release held token
					String cryptoType = cryptoTransaction.getCryptoType();
					double cryptoAmount = Double.valueOf(cryptoTransaction.getCryptoAmount());
					int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
					balanceDao.deductHoldBalance(userId, tokenId, cryptoAmount);
				} else {
					// hold -> release
					String cryptoType = cryptoTransaction.getCryptoType();
					Double cryptoAmount = Double.valueOf(cryptoTransaction.getCryptoAmount());

					int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
					CryptoBalance balance = balanceDao.selectById(userId, tokenId);
					if(balance.getHold() < cryptoAmount) {
						continue;
					}
					balanceDao.releaseHoldBalance(userId, tokenId, cryptoAmount);
				}
			}

			// check wallet payment
			Map<String, BidHolding> walletPayments = bid.getHoldingList();
			Set<String> keySet = walletPayments.keySet();
			for (String _cryptoType : keySet) {
				BidHolding holding = walletPayments.get(_cryptoType);
				if (bid.getStatus() == Bid.WINNER) {
					totalPrice += holding.getUsd();
					
					double cryptoAmount = holding.getCrypto();
					int tokenId = tokenAssetService.getTokenIdBySymbol(_cryptoType);
					balanceDao.deductHoldBalance(userId, tokenId, cryptoAmount);

				} else {

					double cryptoAmount = holding.getCrypto();
					int tokenId = tokenAssetService.getTokenIdBySymbol(_cryptoType);
					CryptoBalance balance = balanceDao.selectById(userId, tokenId);
					if(balance.getHold() < cryptoAmount) {
						continue;
					}
					balanceDao.releaseHoldBalance(userId, tokenId, cryptoAmount);
				}
			}

			// checking Round AVATAR
			boolean roundAvatarWinner = true;
			UserAvatar userAvatar = userAvatarDao.selectById(userId);
			String purchasedJsonString;
			if (userAvatar != null && (purchasedJsonString = userAvatar.getPurchased()) != null
					&& !purchasedJsonString.isEmpty()) {
				JsonObject purchasedJson = JsonParser.parseString(purchasedJsonString).getAsJsonObject();
				for (AvatarComponent component : avatarComponents)
					block_c: {
						JsonElement el = purchasedJson.get(String.valueOf(component.getGroupId()));
						if (el == null || el.isJsonNull()) {
							roundAvatarWinner = false;
							break;
						}
						JsonArray array = el.getAsJsonArray();
						for (JsonElement e : array) {
							if (e.getAsString().equals(component.getCompId())) {
								break block_c;
							}
						}
						roundAvatarWinner = false;
					}
			}

			// check total price and NDB wallet!!
			if (bid.getStatus() == Bid.WINNER) {
				long tokenPrice = bid.getTokenPrice();
				long token = totalPrice / tokenPrice;
				if (roundAvatarWinner) {
					token += avatarToken;
				}

				if (totalToken < token) {
					// add ndb
					int ndbId = tokenAssetService.getTokenIdBySymbol("NDB");
					balanceDao.addFreeBalance(userId, ndbId, totalToken);
					totalToken = 0;
				} else {
					int ndbId = tokenAssetService.getTokenIdBySymbol("NDB");
					balanceDao.addFreeBalance(userId, ndbId, token);
					totalToken -= token;
				}

			}

			// save bid ranking!
			bidDao.updateRanking(bid.getUserId(), bid.getRoundId(), bid.getRanking());

			notificationService.sendNotification(
				bid.getUserId(), 
				Notification.BID_CLOSED, 
				"BID CLOSED", 
				String.format("Your Bid closed. You %s", bid.getStatus() == Bid.WINNER ? "won!" : "failed")
			);
		}
	}

	public Bid increaseBid(int userId, int roundId, long tokenAmount, long tokenPrice) {
		
		Bid originalBid = bidDao.getBid(userId, roundId);
		if (originalBid == null) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		if (originalBid.isPendingIncrease()) {
			// throw new BidException("", "roundId");
			originalBid.setPendingIncrease(false);
		}

		long _tokenAmount = originalBid.getTokenAmount();
		long _tokenPrice = originalBid.getTokenPrice();

		// check amount & price
		if ((_tokenAmount > tokenAmount) ||
				(_tokenPrice > tokenPrice) ||
				(_tokenPrice == tokenPrice && _tokenAmount == tokenAmount)) {
			throw new BidException("New price must be larger than original price.", "tokenPrice");
		}

		// previous total amount!
		long _total = _tokenAmount * _tokenPrice;
		
		// new total amount!
		long newTotal = tokenAmount * tokenPrice;
		
		// more paid
		double delta = newTotal - _total;

		// // check pay type : WALLET!!!!!
		// if (payType == Bid.WALLET) {
		// 	// check user's wallet!

		// 	// get crypto price!!
		// 	double cryptoAmount = delta / cryptoService.getCryptoPriceBySymbol(cryptoType);
		// 	int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
		// 	CryptoBalance balance = balanceDao.selectById(userId, tokenId);

		// 	if (balance.getFree() < cryptoAmount) {
		// 		throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
		// 	}
		// 	balanceDao.makeHoldBalance(userId, tokenId, cryptoAmount);

		// 	Map<String, BidHolding> holdingList = originalBid.getHoldingList();
		// 	BidHolding hold = null;
		// 	if (holdingList.containsKey(cryptoType)) {
		// 		hold = holdingList.get(cryptoType);
		// 		double currentAmount = hold.getCrypto();
		// 		hold.setCrypto(currentAmount + cryptoAmount);
		// 	} else {
		// 		hold = new BidHolding(cryptoAmount, delta);
		// 		holdingList.put(cryptoType, hold);
		// 	}
		// 	bidDao.updateBid(originalBid);
		// 	updateBidRanking(originalBid);

		// 	return originalBid;
		// }

		bidDao.updateTemp(userId, roundId, tokenAmount, tokenPrice);

		return originalBid;
	}

	public int increaseAmount(int userId, int roundId, long tokenAmount, long tokenPrice) {
		return bidDao.increaseAmount(userId, roundId, tokenAmount, tokenPrice);
	}

	public List<Bid> getBidList() {
		return bidDao.getBidList();
	}

	public List<Bid> getBidListFrom(Long from) {
		return bidDao.getBidListFrom(from);
	}

}
