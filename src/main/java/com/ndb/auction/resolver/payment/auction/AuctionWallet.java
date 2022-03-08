package com.ndb.auction.resolver.payment.auction;

import java.util.Map;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;
import com.ndb.auction.models.user.User;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionWallet extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    // NDB Wallet
	@PreAuthorize("isAuthenticated()")
	public String payWalletForAuction(int roundId, String cryptoType) {
		Auction auction = auctionService.getAuctionById(roundId);
		if(auction == null) {
			throw new AuctionException("no_auction", "roundId");
		}
		if(auction.getStatus() != Auction.STARTED) {
			throw new AuctionException("not_started", "roundId");
		}

		// Get Bid
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		
		Bid bid = bidService.getBid(roundId, userId);
		User user = userService.getUserById(userId);
		if(bid == null) throw new BidException("no_bid", "roundId");

		// Get total order in USD
		double totalOrder = 0.0;
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		if(bid.isPendingIncrease()) {
			double delta = bid.getDelta();
			totalOrder = 100 * delta / (100 - tierFeeRate);
		} else {
			double totalPrice = (double) (bid.getTokenPrice() * bid.getTokenAmount());
			totalOrder = 100 * totalPrice / (100 - tierFeeRate);
		}

		// check crypto Type balance
		double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
		double cryptoAmount = totalOrder / cryptoPrice; // required amount!
		double freeBalance = internalBalanceService.getFreeBalance(userId, cryptoType);
		if(freeBalance < cryptoAmount) throw new BidException("insufficient", "amount");

		// make hold
		internalBalanceService.makeHoldBalance(userId, cryptoType, cryptoAmount);
		
		// update holding list
		Map<String, BidHolding> holdingList = bid.getHoldingList();
		BidHolding hold = null;
		if(holdingList.containsKey(cryptoType)) {
			hold = holdingList.get(cryptoType);
			double currentAmount = hold.getCrypto();
			hold.setCrypto(currentAmount + cryptoAmount);
		} else {
			hold = new BidHolding(cryptoAmount, totalOrder);
			holdingList.put(cryptoType, hold);
		}

		// update bid
		bidService.updateHolding(bid);
		long newAmount = bid.getTempTokenAmount();
		long newPrice = bid.getTempTokenPrice();
		bidService.increaseAmount(userId, roundId, newAmount, newPrice);

		// update bid Ranking
		bid.setTokenAmount(newAmount);
		bid.setTokenPrice(newPrice);
		bidService.updateBidRanking(bid);
		return "SUCCESS";
	}
}
