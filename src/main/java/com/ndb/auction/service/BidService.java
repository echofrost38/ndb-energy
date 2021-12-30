package com.ndb.auction.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;
import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.User;
import com.ndb.auction.models.UserTier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.Wallet;
import com.ndb.auction.service.interfaces.IBidService;
import com.ndb.auction.utils.Sort;

/**
 * TODOs
 * 1. UpdateBid Logic 
 * 2. Bid payment status
 * 3. Notification
 * @author klinux
 *
 */

@Service
public class BidService extends BaseService implements IBidService {
	
	@Autowired
	private Sort sort;
	
	@Autowired
	private StripeService stripeService;

	@Autowired
	private CryptoService cryptoService;

	@Override
	public Bid placeNewBid(
			String userId, 
			String roundId, 
			Double tokenAmount, 
			Double tokenPrice,
			Integer payType,
			String cryptoType
	) {
		// Check existing
		Bid bid = bidDao.getBid(roundId, userId);

		if(bid != null) {
			throw new BidException("Already place a bid to this round.", "roundId");
		}

		// create new pending bid
		double totalPrice = tokenAmount * tokenPrice;
		bid = new Bid(userId, roundId, tokenAmount, tokenPrice);
		
		// check Round is opened. 
		Auction auction = auctionDao.getAuctionById(roundId);
		if(auction.getStatus() != Auction.STARTED) {
			throw new BidException("Round is not yet started.", "roundId");
		}

		if(auction.getMinPrice() > tokenPrice) {
			throw new BidException("Token price must be larget than min price.", "tokenPrice");
		}
		
		if(payType == Bid.CRYPTO) {
			long leftTime = auction.getEndedAt() - System.currentTimeMillis();
			if(leftTime < 10 * 60 * 1000) {
				throw new BidException("Bid must be placed 10 minutes before the end of round.", "roundId");
			}
		}
		
		// set bid type
		bid.setPayType(payType);

		// check pay type : WALLET!!!!!
		if(payType == Bid.WALLET) {
			// check user's wallet!
			double cryptoAmount = totalPrice / cryptoService.getCryptoPriceBySymbol(cryptoType);
			
//			User user = userDao.getUserById(userId);
//			Map<String, Wallet> wallets = user.getWallet();
//			Wallet wallet = wallets.get(cryptoType);
//			if(wallet == null) {
//				wallet = new Wallet();
//				wallets.put(cryptoType, wallet);
//			}
//			
//			
//			double holding = wallet.getHolding();
//			double free = wallet.getFree();
//			if(free < cryptoAmount) {
//				throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
//			}
//
//			wallet.setFree(free - cryptoAmount);
//			wallet.setHolding(holding + cryptoAmount);
//			
//			userDao.updateUser(user);
			
			Wallet wallet = userWalletService.getWalletById(userId, cryptoType);
			double free = wallet.getFree();
			if(free < cryptoAmount) {
				throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
			}
			TransactionReceipt receipt = userWalletService.makeHold(userId, cryptoType, cryptoAmount);
			List<Log> logs = receipt.getLogs();
			if(logs.isEmpty()) throw new BidException("We cannot make the hold in your wallet.", "tokenAmount");
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

	private void addAuctionPoints(String userId, int roundNumber) {
		TierTask tierTask = tierService.getTierTask(userId);
		TaskSetting taskSetting = tierService.getTaskSetting();
		List<UserTier> tiers = tierService.getUserTiers();

		if(tierTask.getAuctions().contains(roundNumber)) {
			return;
		}
		User user = userDao.getUserById(userId);
		tierTask.getAuctions().add(roundNumber);
		double points = user.getTierPoints();
		points += taskSetting.getAuction();
		double _points = points;
		for (UserTier tier : tiers) {
			if(tier.getPoints() >= points && tier.getPoints() > _points) {
				_points = tier.getPoints();
				user.setTierLvl(tier.getLevel());
			}
		}
		user.setTierPoints(points);
		tierService.updateTierTask(tierTask);
		userDao.updateUser(user);
	}

	@Override
	public List<Bid> getBidListByRound(Integer round) {
		// PaginatedScanList<> how to sort?
		Auction auction = auctionDao.getAuctionByRound(round);
		Bid[] bidList = bidDao.getBidListByRound(auction.getAuctionId()).toArray(new Bid[0]);
		Arrays.sort(bidList, Comparator.comparingDouble(Bid::getTokenPrice).reversed());
		return Arrays.asList(bidList);
	}

	public List<Bid> getBidListByRoundId(String round) {
		return bidDao.getBidListByRound(round);
	}

	@Override
	public List<Bid> getBidListByUser(String userId) {
		// User's bidding history
		return bidDao.getBidListByUser(userId);
	}

	@Override
	public Bid getBid(Integer round, String userId) {
		return bidDao.getBid(round, userId);
	}

	public Bid getBid(String roundId, String userId) {
		return bidDao.getBid(roundId, userId);
	}

	@Override
	public Bid updateBid(String userId, String roundId, Double tokenAmount, Double tokenPrice) {
		
		Bid bid = bidDao.getBid(roundId, userId);
		
		// check null 
		if(bid == null) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}
		
		// 
		
		bid.setTokenAmount(tokenAmount);
		bid.setTokenPrice(tokenPrice);
		bid.setTotalPrice(tokenPrice * tokenAmount);
		bid.setPendingIncrease(false);

		return bidDao.updateBid(bid);
	}

	/**
	 * It is called from Payment service with user id and round number.
	 */
	@Override
	public void updateBidRanking(String userId, String roundId) {
		
		Auction currentRound = auctionDao.getAuctionById(roundId);
		
		addAuctionPoints(userId, currentRound.getNumber());

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
		double qty = 0.0, win = 0.0, fail = 0.0;
		final double total = currentRound.getTotalToken();
		double availableToken = total;
		
		int len = newList.length;
		for(int i = 0; i < len; i++) {
			if(status) {
				win += newList[i].getTokenPrice();
				newList[i].setStatus(Bid.WINNER);
			} else {
				fail += newList[i].getTokenPrice();
				newList[i].setStatus(Bid.FAILED);
			}
			availableToken -= bid.getTokenAmount();
			
			if(availableToken < 0) {
				status = false; // change to fail
				win -= availableToken;
				fail += availableToken;
			}   
		}
		
		// Save new Bid status
		bidDao.updateBidStatus(newList);
		
        // update & save new auction stats
		currentRound.setStats(new AuctionStats(qty, win, fail));       
        if(win > total) {
        	currentRound.setSold(total);
        } else {
        	currentRound.setSold(win);
        }
		auctionDao.updateAuctionStats(currentRound);
        
        // send Notification
        notificationService.sendNotification(
			userId,
			Notification.N_BID_RANKING_UPDATED, 
			"Bid Ranking Updated", 
			"Bid ranking is updated, please check your bid ranking"
		);
	}
	
	// not sychnorized
	public void closeBid(String roundId) {
		
		Auction auction = auctionDao.getAuctionById(roundId);
		List<AvatarComponent> avatarComponents = 
			avatarDao.getAvatarComponentsBySet(auction.getAvatar());
		Double avatarToken = auction.getToken();
		double totalToken = auction.getTotalToken();
		
		// Assume all status already confirmed when new bid is placed
		List<Bid> _bidList = bidDao.getBidListByRound(roundId);
		Bid bids[] = new Bid[_bidList.size()];
		_bidList.toArray(bids);
		
		List<Bid> bidList = Arrays.asList(bids);
		
		// processing all bids
		ListIterator<Bid> iterator = bidList.listIterator();
	    while (iterator.hasNext()) {
	    	
	        Bid bid = iterator.next();
	        
	        String userId = bid.getUserId();
			double totalPrice = 0.0;

			// check stripe
			List<StripeTransaction> fiatTxns = stripeService.getTransactions(roundId, userId);
			for (StripeTransaction fiatTransaction : fiatTxns) {
				boolean result = stripeService.UpdateTransaction(fiatTransaction.getId(), bid.getStatus());
				if(result && (bid.getStatus() == Bid.WINNER)) {
					totalPrice += fiatTransaction.getAmount();					
				}
			}

			// check crypto
			User user = userDao.getUserById(userId);
//			Map<String, Wallet> tempWallet = user.getWallet();

			List<CryptoTransaction> cryptoTxns = cryptoService.getTransaction(roundId, userId);
			for (CryptoTransaction cryptoTransaction : cryptoTxns) {
				if(cryptoTransaction.getStatus() != CryptoTransaction.CONFIRMED) {
					continue;
				}
				if(bid.getStatus() == Bid.WINNER) {
					totalPrice += cryptoTransaction.getAmount();
				} else {
					// hold -> release
					String cryptoType = cryptoTransaction.getCryptoType();
					double cryptoAmount = cryptoTransaction.getCryptoAmount();

					Wallet wallet = userWalletService.getWalletById(userId, cryptoType);

					double hold = wallet.getHolding();
					if(hold > cryptoAmount) {
						userWalletService.releaseHold(userId, cryptoType, cryptoAmount);
					} else {
						continue;
					}
				}
			}
			
	        // check wallet payment
			Map<String, BidHolding> walletPayments = bid.getHoldingList();
			Set<String> keySet = walletPayments.keySet();
			for (String _cryptoType : keySet) {
				BidHolding holding = walletPayments.get(_cryptoType);
				if(bid.getStatus() == Bid.WINNER) {
					totalPrice += holding.getUsd();
				} else {
					// hold -> release
					double cryptoAmount = holding.getCrypto();
					Wallet wallet = userWalletService.getWalletById(userId, _cryptoType);
					
					double hold = wallet.getHolding();
					if(hold > cryptoAmount) {
						userWalletService.releaseHold(userId, _cryptoType, cryptoAmount);
					} else {
						continue;
					}
				}
			}

			// checking Round AVATAR
			boolean roundAvatarWinner = true;
			Map<String, List<String>> userAvatarPurchased = user.getAvatarPurchase();
			for (AvatarComponent component : avatarComponents) {
				List<String> list = userAvatarPurchased.get(component.getGroupId());
				
				if(list == null) {
					roundAvatarWinner = false;
					break;
				};
				
				if(!list.contains(component.getCompId())) {
					roundAvatarWinner = false;
				}
			}			
			
			// check total price and NDB wallet!!
	        if(bid.getStatus() == Bid.WINNER) {
				double tokenPrice = bid.getTokenPrice();
				double token = totalPrice / tokenPrice;
				if(roundAvatarWinner) {
					token += avatarToken;
				}
				
				if(totalToken < token) {
					userWalletService.addFreeAmount(userId, "NDB", totalToken);
					totalToken = 0;
				} else {
					userWalletService.addFreeAmount(userId, "NDB", totalToken);
					totalToken -= token;
				}
				
			}
			
			userDao.updateUser(user);

			// send notification
	        notificationService.sendNotification(
				userId,
				Notification.N_BID_CLOSED, 
				"Bid Closed", 
				"Please check you bid result"
			);
	    }
	}

	@Override
	public Bid increaseBid(String userId, String roundId, Double tokenAmount, Double tokenPrice, Integer payType,
			String cryptoType) 
	{
		Bid originalBid = bidDao.getBid(roundId, userId);
		if(originalBid == null) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		if(originalBid.getPendingIncrease()) {
			throw new BidException("Bid is not yet placed.", "roundId");
		}

		double _tokenAmount = originalBid.getTokenAmount();
		double _tokenPrice = originalBid.getTokenPrice();

		// check amount & price 
		if(
			(_tokenAmount > tokenAmount) || 
			(_tokenPrice > tokenPrice) ||
			(_tokenPrice == tokenPrice && _tokenAmount == tokenAmount)
		) {
			throw new BidException("New price must be larger than original price.", "tokenPrice");
		}

		double _total = _tokenAmount * _tokenPrice;
		double newTotal = tokenAmount * tokenPrice;
		double delta = newTotal - _total;

		// check pay type : WALLET!!!!!
		if(payType == Bid.WALLET) {
			// check user's wallet!

			// get crypto price!!
			double cryptoAmount = 
				delta / cryptoService.getCryptoPriceBySymbol(cryptoType);

			Wallet wallet = userWalletService.getWalletById(userId, cryptoType);
			
			double free = wallet.getFree();
			if(free < cryptoAmount) {
				throw new BidException("You don't have enough balance in wallet.", "tokenAmount");
			}
			userWalletService.makeHold(userId, cryptoType, free);
			
			Map<String, BidHolding> holdingList = originalBid.getHoldingList();
			BidHolding hold = null;
			if (holdingList.containsKey(cryptoType)) {
				hold = holdingList.get(cryptoType);
				double currentAmount = hold.getCrypto();
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

}
