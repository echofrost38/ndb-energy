package com.ndb.auction.service;

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
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Wallet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.utils.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

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
	private StripeService stripeService;

	@Autowired
	private CryptoService cryptoService;

	public Bid placeNewBid(
			int userId,
			int roundId,
			long tokenAmount,
			long tokenPrice,
			int payType,
			String cryptoType) {
		// Check existing
		Bid bid = bidDao.getBid(roundId, userId);
		if (bid != null) {
			throw new BidException("Already place a bid to this round.", "roundId");
		}

		// create new pending bid
		long totalPrice = tokenAmount * tokenPrice;
		bid = new Bid(userId, roundId, tokenAmount, tokenPrice);

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

		if (payType == Bid.CRYPTO) {
			long leftTime = auction.getEndedAt() - System.currentTimeMillis();
			if (leftTime < 10 * 60 * 1000) {
				throw new BidException("Bid must be placed 10 minutes before the end of round.", "roundId");
			}
		}

		// set bid type
		bid.setPayType(payType);

		// check pay type : WALLET!!!!!
		if (payType == Bid.WALLET) {
			// check user's wallet!
			long cryptoAmount = totalPrice / cryptoService.getCryptoPriceBySymbol(cryptoType);

			// User user = userDao.selectById(userId);
			// Map<String, Wallet> wallets = user.getWallet();
			// Wallet wallet = wallets.get(cryptoType);
			// if(wallet == null) {
			// wallet = new Wallet();
			// wallets.put(cryptoType, wallet);
			// }
			//
			//
			// double holding = wallet.getHolding();
			// long free = wallet.getFree();
			// if(free < cryptoAmount) {
			// throw new BidException("You don't have enough balance in wallet.",
			// "tokenAmount");
			// }
			//
			// wallet.setFree(free - cryptoAmount);
			// wallet.setHolding(holding + cryptoAmount);
			//
			// userDao.updateUser(user);

			Wallet wallet = userWalletService.getWalletById(userId, cryptoType);
			long free = wallet.getFree();
			if (free < cryptoAmount) {
				throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
			}
			TransactionReceipt receipt = userWalletService.makeHold(userId, cryptoType, cryptoAmount);
			List<Log> logs = receipt.getLogs();
			if (logs.isEmpty())
				throw new BidException("We cannot make the hold in your wallet.", "tokenAmount");
			Log log = logs.get(0);
			String data = log.getData();
			System.out.println(data);

			Map<String, BidHolding> holdingList = bid.getHoldingList();
			BidHolding hold = new BidHolding(cryptoAmount, totalPrice);
			holdingList.put(cryptoType, hold);
			bid.setHoldingList(holdingList);

			bidDao.placeBid(bid);
			updateBidRanking(userId, roundId);
			return bid;
		}

		// save with pending status
		bidDao.placeBid(bid);
		return bid;
	}

	private void addAuctionpoint(int userId, int roundNumber) {
		TierTask tierTask = tierTaskService.getTierTask(userId);
		TaskSetting taskSetting = taskSettingService.getTaskSetting();
		List<Tier> tiers = tierService.getUserTiers();

		if (tierTask.getAuctions().contains(roundNumber)) {
			return;
		}
		User user = userDao.selectById(userId);
		tierTask.getAuctions().add(roundNumber);
		long point = user.getTierPoint();
		point += taskSetting.getAuction();
		long _point = point;
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

		List<Bid> bidList = bidDao.getBidListByRound(auction.getId());
		Bid _bidArray[] = new Bid[bidList.size()];
		bidList.toArray(_bidArray);
		sort.mergeSort(_bidArray, 0, _bidArray.length - 1);
		return Arrays.asList(_bidArray);
	}

	public List<Bid> getBidListByRoundId(int round) {
		return bidDao.getBidListByRound(round);
	}

	public List<Bid> getBidListByUser(int userId) {
		// User's bidding history
		return bidDao.getBidListByUser(userId);
	}

	public Bid getBid(Integer round, int userId) {
		return bidDao.getBid(userId, round);
	}

	public Bid getBid(int roundId, int userId) {
		return bidDao.getBid(userId, roundId);
	}

	public Bid updateBid(int userId, int roundId, long tokenAmount, long tokenPrice) {

		Bid bid = bidDao.getBid(userId, roundId);

		// check null
		if (bid == null) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		bid.setTokenAmount(tokenAmount);
		bid.setTokenPrice(tokenPrice);
		bid.setTotalPrice(tokenPrice * tokenAmount);
		bid.setPendingIncrease(false);

		return bidDao.updateBid(bid);
	}

	/**
	 * It is called from Payment service with user id and round number.
	 */
	public void updateBidRanking(int userId, int roundId) {

		Auction currentRound = auctionDao.getAuctionById(roundId);

		addAuctionpoint(userId, currentRound.getRound());

		// sorting must be updated!!!!
		Bid bid = bidDao.getBid(roundId, userId);
		List<Bid> bidList = bidDao.getBidListByRound(roundId);
		Bid _bidArray[] = new Bid[bidList.size()];
		bidList.toArray(_bidArray);
		Bid newList[] = Arrays.copyOf(_bidArray, _bidArray.length + 1);
		newList[_bidArray.length] = bid;

		// Sort Bid List by
		// 1. Token Price
		// 2. Total Price ( Amount of pay )
		// 3. Placed time ( early is winner )
		sort.mergeSort(newList, 0, newList.length - 1);

		// true : winner, false : fail
		boolean status = true;

		// qty, win, fail : total price ( USD )
		long qty = 0, win = 0, fail = 0;
		final long total = currentRound.getTotalToken();
		long availableToken = total;

		int len = newList.length;
		for (int i = 0; i < len; i++) {
			boolean statusChanged = false;
			Bid tempBid = newList[i];
			
			if (status) {
				win += newList[i].getTokenPrice();
				if(tempBid.getStatus() != Bid.WINNER) {
					tempBid.setStatus(Bid.WINNER);
					statusChanged = true;
				}
			} else {
				fail += newList[i].getTokenPrice();
				if(tempBid.getStatus() != Bid.FAILED) {
					tempBid.setStatus(Bid.FAILED);
					statusChanged = true;
				}
			}
			availableToken -= bid.getTokenAmount();

			if (availableToken < 0) {
				status = false; // change to fail
				win -= availableToken;
				fail += availableToken;
			}

			if(statusChanged) {
				bidDao.updateBid(tempBid);
	
				// send Notification
				notificationService.sendNotification(
					tempBid.getUserId(),
					Notification.BID_RANKING_UPDATED,
					"BID RANKING UPDATED",
					String.format("Bid ranking is updated into %d, your bid is %d.", 
						i, tempBid.getStatus() == Bid.WINNER ? "WINNER" : "FAILED")
				);
			}
			
		}

		// update & save new auction stats
		currentRound.setStats(new AuctionStats(qty, win, fail));
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

		// Assume all status already confirmed when new bid is placed
		List<Bid> _bidList = bidDao.getBidListByRound(roundId);
		Bid bids[] = new Bid[_bidList.size()];
		_bidList.toArray(bids);

		sort.mergeSort(bids, 0, bids.length - 1);

		List<Bid> bidList = Arrays.asList(bids);

		// processing all bids
		ListIterator<Bid> iterator = bidList.listIterator();
		while (iterator.hasNext()) {

			Bid bid = iterator.next();

			int userId = bid.getUserId();
			long totalPrice = 0;

			// check stripe
			List<StripeTransaction> fiatTxns = stripeService.getTransactions(roundId, userId);
			for (StripeTransaction fiatTransaction : fiatTxns) {

				// Processing Stripe CAPTURE or CANCEL
				boolean result = stripeService.UpdateTransaction(fiatTransaction.getId(), bid.getStatus());
				if (result && (bid.getStatus() == Bid.WINNER)) {
					totalPrice += fiatTransaction.getAmount();
				}
			}

			// check crypto
			User user = userDao.selectById(userId);
			// Map<String, Wallet> tempWallet = user.getWallet();

			List<CryptoTransaction> cryptoTxns = cryptoService.getTransaction(roundId, userId);
			for (CryptoTransaction cryptoTransaction : cryptoTxns) {
				if (cryptoTransaction.getStatus() != CryptoTransaction.CONFIRMED) {
					continue;
				}
				if (bid.getStatus() == Bid.WINNER) {
					String sAmount = cryptoTransaction.getAmount();
					totalPrice += Long.valueOf(sAmount);
				} else {
					// hold -> release
					String cryptoType = cryptoTransaction.getCryptoType();
					String cryptoAmount = cryptoTransaction.getCryptoAmount();

					Wallet wallet = userWalletService.getWalletById(userId, cryptoType);

					long hold = wallet.getHolding();
					// if (hold > cryptoAmount) {
					// 	userWalletService.releaseHold(userId, cryptoType, cryptoAmount);
					// } else {
					// 	continue;
					// }
				}
			}

			// check wallet payment
			Map<String, BidHolding> walletPayments = bid.getHoldingList();
			Set<String> keySet = walletPayments.keySet();
			for (String _cryptoType : keySet) {
				BidHolding holding = walletPayments.get(_cryptoType);
				if (bid.getStatus() == Bid.WINNER) {
					totalPrice += holding.getUsd();
				} else {
					// hold -> release
					long cryptoAmount = holding.getCrypto();
					Wallet wallet = userWalletService.getWalletById(userId, _cryptoType);

					long hold = wallet.getHolding();
					if (hold > cryptoAmount) {
						userWalletService.releaseHold(userId, _cryptoType, cryptoAmount);
					} else {
						continue;
					}
				}
			}

			// checking Round AVATAR
			boolean roundAvatarWinner = true;
			UserAvatar userAvatar = userAvatarDao.selectById(user.getId());
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
					// userWalletService.addFreeAmount(userId, "NDB", totalToken);
					totalToken = 0;
				} else {
					// userWalletService.addFreeAmount(userId, "NDB", totalToken);
					totalToken -= token;
				}

			}

			// userDao.updateUser(user); //TODO: why update?
			notificationService.sendNotification(
				bid.getUserId(), 
				Notification.BID_CLOSED, 
				"BID CLOSED", 
				String.format("Your Bid closed. You %s", bid.getStatus() == Bid.WINNER ? "won!" : "failed")
			);

			
		}
	}

	public Bid increaseBid(int userId, int roundId, long tokenAmount, long tokenPrice, int payType,
			String cryptoType) {
		Bid originalBid = bidDao.getBid(userId, roundId);
		if (originalBid == null) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		if (originalBid.isPendingIncrease()) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		long _tokenAmount = originalBid.getTokenAmount();
		long _tokenPrice = originalBid.getTokenPrice();

		// check amount & price
		if ((_tokenAmount > tokenAmount) ||
				(_tokenPrice > tokenPrice) ||
				(_tokenPrice == tokenPrice && _tokenAmount == tokenAmount)) {
			throw new BidException("New price must be larger than original price.", "tokenPrice");
		}

		long _total = _tokenAmount * _tokenPrice;
		long newTotal = tokenAmount * tokenPrice;
		long delta = newTotal - _total;

		// check pay type : WALLET!!!!!
		if (payType == Bid.WALLET) {
			// check user's wallet!

			// get crypto price!!
			long cryptoAmount = delta / cryptoService.getCryptoPriceBySymbol(cryptoType);

			Wallet wallet = userWalletService.getWalletById(userId, cryptoType);

			long free = wallet.getFree();
			if (free < cryptoAmount) {
				throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
			}
			userWalletService.makeHold(userId, cryptoType, free);

			Map<String, BidHolding> holdingList = originalBid.getHoldingList();
			BidHolding hold = null;
			if (holdingList.containsKey(cryptoType)) {
				hold = holdingList.get(cryptoType);
				long currentAmount = hold.getCrypto();
				hold.setCrypto(currentAmount + cryptoAmount);
			} else {
				hold = new BidHolding(cryptoAmount, delta);
				holdingList.put(cryptoType, hold);
			}
			bidDao.updateBid(originalBid);
			updateBidRanking(userId, roundId);

			return originalBid;
		}

		originalBid.setTempTokenAmount(tokenAmount);
		originalBid.setTempTokenPrice(tokenPrice);
		originalBid.setDelta(delta);
		originalBid.setPendingIncrease(true);
		bidDao.updateBid(originalBid);

		return originalBid;
	}

	public List<Bid> getBidList() {
		return bidDao.getBidList();
	}

}
