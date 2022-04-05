package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.gson.reflect.TypeToken;
import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.models.user.Whitelist;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentAuctionService;
import com.ndb.auction.service.payment.stripe.StripeAuctionService;
import com.ndb.auction.utils.Sort;
import com.stripe.model.PaymentIntent;

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
			double tokenAmount,
			double tokenPrice) {
		// Check existing
		Bid bid = bidDao.getBid(userId, roundId);
		if (bid != null && bid.getStatus() != Bid.NOT_CONFIRMED) {
			String msg = messageSource.getMessage("already_placed", null, Locale.ENGLISH);
            throw new AuctionException(msg, "bid");
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
			String msg = messageSource.getMessage("no_auction", null, Locale.ENGLISH);
            throw new AuctionException(msg, "auction");
		}

		if (auction.getStatus() != Auction.STARTED) {
			String msg = messageSource.getMessage("not_started", null, Locale.ENGLISH);
            throw new AuctionException(msg, "auction");
		}

		if (auction.getMinPrice() > tokenPrice) {
			String msg = messageSource.getMessage("invalid_bid_price", null, Locale.ENGLISH);
            throw new AuctionException(msg, "auction");
		}

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
			if (tier.getPoint() <= point && tier.getPoint() <= _point) {
				_point = tier.getPoint();
				level = tier.getLevel();
			}
		}
		tierTaskService.updateTierTask(tierTask); // TODO: why update?
		var tier = tierDao.selectByLevel(level);
		if(tier.getName().equals("Diamond")) {
			var m = whitelistDao.selectByUserId(userId);
            if(m == null) {
                m = new Whitelist(userId, "Diamond Level");
                whitelistDao.insert(m);
            }
		}
		userDao.updateTier(userId, level, point);
	}

	public List<Bid> getBidListByRound(int round) {
		// PaginatedScanList<> how to sort?
		Auction auction = auctionDao.getAuctionByRound(round);
		if(auction == null) {
			String msg = messageSource.getMessage("no_auction", null, Locale.ENGLISH);
            throw new AuctionException(msg, "auction");
		}

		if(auction.getStatus() == Auction.STARTED) {
			if(currentBidList == null) fillBidList(auction.getId());
			if((currentBidList.size() != 0) && (currentBidList.get(0).getRound() != round)) fillBidList(auction.getId());
			return currentBidList;
		}
		return bidDao.getBidListByRound(auction.getId());
	}

	public List<Bid> getBidListByRoundId(int round) {
		// check round status
		Auction currentRound = auctionDao.getAuctionById(round);
		if(currentRound == null) {
			String msg = messageSource.getMessage("no_auction", null, Locale.ENGLISH);
            throw new AuctionException(msg, "auction");
		}

		if(currentRound.getStatus() == Auction.STARTED) {
			if(currentBidList == null) fillBidList(round);
			if((currentBidList.size() != 0) && (currentBidList.get(0).getRoundId() != round)) fillBidList(round);
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
				break;
			}
		}
		if(!exists) {
			currentBidList.add(bid);
		}

		bidDao.updateStatus(userId, roundId, bid.getPayType(), 1);
		
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

			if (availableToken < 0 && status) {
				status = false; // change to fail
				win -= Math.abs(availableToken);
				fail += Math.abs(availableToken);
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
	@SuppressWarnings("unchecked")
	public void closeBid(int roundId) {

		Auction auction = auctionDao.getAuctionById(roundId);
		List<AvatarSet> avatar = auctionAvatarDao.selectById(auction.getId());

		// processing all bids
		if(currentBidList == null) fillBidList(roundId);
		ListIterator<Bid> iterator = currentBidList.listIterator();
		while (iterator.hasNext()) {

			Bid bid = iterator.next();
			int userId = bid.getUserId();
			Boolean captureError = false;
			// check bid status 
			// A) if winner 
			if(bid.getStatus() == Bid.WINNER) {
				
				// 1) check Stripe transaction to capture!
				List<StripeAuctionTransaction> stripeTxns = stripeService.selectByIds(roundId, userId);
				for (StripeAuctionTransaction stripeTransaction : stripeTxns) {
					try {
						PaymentIntent intent = PaymentIntent.retrieve(stripeTransaction.getPaymentIntentId());
						intent.capture();
					} catch (Exception e) {
						captureError = true;
						break;
					}
				}

				// 2) check Coinpayment remove hold token
				List<CoinpaymentAuctionTransaction> coinpaymentTxns = (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
				for (CoinpaymentAuctionTransaction coinpaymentTxn : coinpaymentTxns) {
					// get crypto type and amount
					String cryptoType = coinpaymentTxn.getCryptoType();
					Double cryptoAmount = coinpaymentTxn.getCryptoAmount();

					// deduct hold value
					Integer tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
					if(tokenId == null) {
						captureError = true;
						break;
					}
					balanceDao.deductHoldBalance(userId, tokenId, cryptoAmount);
				}

				// 4) Wallet payment holding
				Map<String, BidHolding> holdingList = bid.getHoldingList();
				Set<String> keySet = holdingList.keySet();
				for (String key : keySet) {
					BidHolding holding = holdingList.get(key);
					// deduct hold balance
					int tokenId = tokenAssetService.getTokenIdBySymbol(key);
					balanceDao.deductHoldBalance(userId, tokenId, holding.getCrypto());
				}

				// 5) Avatar checking!
				boolean roundAvatarWinner = true;
				UserAvatar userAvatar = userAvatarDao.selectById(userId);
				
				List<AvatarSet> selected = gson.fromJson(userAvatar.getSelected(), new TypeToken<List<AvatarSet>>(){}.getType());

				boolean notFound = true;
				for (AvatarSet roundComp: avatar) {
					notFound = true;
					for (AvatarSet userComp : selected) {
						if(roundComp.equals(userComp)) {
							notFound = false;
							break;
						}
					}
					if(notFound) {
						roundAvatarWinner = false;
						break;
					}
				}
	
				// 6) Allocate NDB Token
				double ndb = bid.getTokenAmount();
				if(roundAvatarWinner) ndb += auction.getToken();
				int ndbId = tokenAssetService.getTokenIdBySymbol("NDB");
				balanceDao.addFreeBalance(userId, ndbId, ndb);

			} else if (bid.getStatus() == Bid.FAILED) { // B) if lost
				// 1) check Stripe transaction to capture!
				List<StripeAuctionTransaction> stripeTxns = stripeService.selectByIds(roundId, userId);
				for (StripeAuctionTransaction stripeTransaction : stripeTxns) {
					try {
						PaymentIntent intent = PaymentIntent.retrieve(stripeTransaction.getPaymentIntentId());
						intent.cancel();
					} catch (Exception e) {
						captureError = true;
						break;
					}
				}

				// 2) check Coinpayment remove hold token
				List<CoinpaymentAuctionTransaction> coinpaymentTxns = (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
				for (CoinpaymentAuctionTransaction coinpaymentTxn : coinpaymentTxns) {
					// get crypto type and amount
					String cryptoType = coinpaymentTxn.getCryptoType();
					Double cryptoAmount = coinpaymentTxn.getCryptoAmount();

					// deduct hold value
					Integer tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
					if(tokenId == null) {
						captureError = true;
						break;
					}
					balanceDao.releaseHoldBalance(userId, tokenId, cryptoAmount);
				}

				// 3) Check Paypal transaction to capture
				List<PaypalAuctionTransaction> paypalTxns = paypalAuctionDao.selectByIds(userId, roundId);
				int usdtId = tokenAssetService.getTokenIdBySymbol("USDT");
				for (PaypalAuctionTransaction paypalTxn : paypalTxns) {
					// convert into USDT balance!
					double paypalAmount = paypalTxn.getAmount();
					balanceDao.addFreeBalance(userId, usdtId, paypalAmount);
				}

				// 4) Wallet payment holding
				Map<String, BidHolding> holdingList = bid.getHoldingList();
				Set<String> keySet = holdingList.keySet();
				for (String key : keySet) {
					BidHolding holding = holdingList.get(key);
					// deduct hold balance
					int tokenId = tokenAssetService.getTokenIdBySymbol(key);
					balanceDao.releaseHoldBalance(userId, tokenId, holding.getCrypto());
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

		// clear current bid list!
		currentBidList = null;
	}

	public Bid increaseBid(int userId, int roundId, double tokenAmount, double tokenPrice) {
		
		Bid originalBid = bidDao.getBid(userId, roundId);
		if (originalBid == null) {
			String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
			throw new AuctionException(msg, "bid");
		}

		if (originalBid.isPendingIncrease()) {
			originalBid.setPendingIncrease(false);
		}

		double _tokenAmount = originalBid.getTokenAmount();
		double _tokenPrice = originalBid.getTokenPrice();

		// check amount & price
		if ((_tokenAmount > tokenAmount) ||
				(_tokenPrice > tokenPrice) ||
				(_tokenPrice == tokenPrice && _tokenAmount == tokenAmount)) {
					String msg = messageSource.getMessage("invalid_increase", null, Locale.ENGLISH);
					throw new AuctionException(msg, "tokenPrice");
		}

		// previous total amount!
		double _total = _tokenAmount * _tokenPrice;
		// new total amount!
		double newTotal = tokenAmount * tokenPrice;		
		// more paid
		double delta = newTotal - _total;

		bidDao.updateTemp(userId, roundId, tokenAmount, tokenPrice, delta);

		return originalBid;
	}

	public int increaseAmount(int userId, int roundId, double tokenAmount, double tokenPrice) {
		return bidDao.increaseAmount(userId, roundId, tokenAmount, tokenPrice);
	}

	public List<Bid> getBidList() {
		return bidDao.getBidList();
	}

	public List<Bid> getBidListFrom(Long from) {
		return bidDao.getBidListFrom(from);
	}

	public int updatePaid(int userId, int auctionId, double paid) {
		return bidDao.updatePaid(userId, auctionId, paid);
	}

	public int updateHolding(Bid bid){
		return bidDao.updateBidHolding(bid);
	}

}
