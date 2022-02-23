package com.ndb.auction.resolver.payment;

import org.apache.http.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.models.transactions.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.StripeAuctionTransaction;
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
	public List<StripeAuctionTransaction> getStripeTransactionsByRound(int roundId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByRound(roundId, null);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeTransactionsByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(id, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeTransactionsByAdmin(int userId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeTransactionByAdmin(int roundId, int userId) {
		return stripeAuctionService.selectByIds(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeTransaction(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeAuctionService.selectByIds(roundId, id);
	}

	@PreAuthorize("isAuthenticated()")
	public PayResponse stripePayment(
		int roundId, 
		Long amount, 
		String paymentIntentId,
		String paymentMethodId
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, amount, paymentIntentId, paymentMethodId);
		return stripeAuctionService.createNewTransaction(m);
	}

	@PreAuthorize("isAuthenticated()")
	public CoinpaymentAuctionTransaction createCryptoPaymentForAuction(int roundId, Long amount, String cryptoType, String network, String coin) throws ParseException, IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		CoinpaymentAuctionTransaction _m = new CoinpaymentAuctionTransaction(roundId, userId, amount, cryptoType, network, coin);
		return (CoinpaymentAuctionTransaction) coinpaymentAuctionService.createNewTransaction(_m);
	}

	// @PreAuthorize("isAuthenticated()")
	public String getExchangeRate() throws ParseException, IOException {
		return cryptoService.getExchangeRate();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public CryptoTransaction getCryptoTransactionById(int id) {
		return cryptoService.getTransactionById(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactionByAdmin(int userId) {
		return cryptoService.getTransactionByUser(userId);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<CryptoTransaction> getCryptoTransactionByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return cryptoService.getTransactionByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactionByRound(int roundId) {
		return cryptoService.getTransactionByRound(roundId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactions(int roundId, int userId) {
		return cryptoService.getTransaction(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CryptoTransaction> getCryptoTransaction(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return cryptoService.getTransaction(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public double getCryptoPrice(String symbol) {
		return cryptoService.getCryptoPriceBySymbol(symbol);
	}

}
