package com.ndb.auction.resolver.payment;

import org.apache.http.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component

public class BidPaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	// for stripe payment
	@PreAuthorize("isAuthenticated()")
	@CrossOrigin
	public String getStripePubKey() {
		return stripeBaseService.getPublicKey();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxByRound(int roundId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByRound(roundId, null);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeAuctionTxByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(id, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxByAdmin(int userId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxForRoundByAdmin(int roundId, int userId) {
		return stripeAuctionService.selectByIds(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeAuctionTx(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeAuctionService.selectByIds(roundId, id);
	}

	@PreAuthorize("isAuthenticated()")
	public PayResponse payStripeForAuction(
		int roundId, 
		Long amount,
		String paymentIntentId,
		String paymentMethodId,
		boolean isSaveCard
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, amount, paymentIntentId, paymentMethodId, isSaveCard);
		return stripeAuctionService.createNewTransaction(m);
	}

	// for Coinpayments
	@PreAuthorize("isAuthenticated()")
	public CoinpaymentAuctionTransaction createCryptoPaymentForAuction(int roundId, Long amount, String cryptoType, String network, String coin) throws ParseException, IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		CoinpaymentAuctionTransaction _m = new CoinpaymentAuctionTransaction(roundId, userId, amount, cryptoType, network, coin);
		return (CoinpaymentAuctionTransaction) coinpaymentAuctionService.createNewTransaction(_m);
	}

	// @PreAuthorize("isAuthenticated()")
	public String getExchangeRate() throws ParseException, IOException {
		return coinpaymentAuctionService.getExchangeRate();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public CoinpaymentAuctionTransaction getCryptoAuctionTxById(int id) {
		return (CoinpaymentAuctionTransaction) coinpaymentAuctionService.selectById(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByAdmin(int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByRound(int roundId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByAuctionId(roundId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxPerRoundByAdmin(int roundId, int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTx(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public double getCryptoPrice(String symbol) {
		return 	thirdAPIUtils.getCryptoPriceBySymbol(symbol);
	}

}
