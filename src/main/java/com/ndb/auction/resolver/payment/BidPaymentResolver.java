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
import com.ndb.auction.models.StripeTransaction;
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
		return stripeService.getPublicKey();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionsByRound(int roundId) {
		return stripeService.getTransactionsByRound(roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeTransaction> getStripeTransactionsByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeService.getTransactionByUser(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionsByAdmin(int userId) {
		return stripeService.getTransactionByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionByAdmin(int roundId, int userId) {
		return stripeService.getTransactions(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeTransaction> getStripeTransaction(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeService.getTransactions(roundId, id);
	}

	@PreAuthorize("isAuthenticated()")
	public PayResponse stripePayment(
		int roundId, 
		Long amount, 
		String paymentIntentId,
		String paymentMethodId
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeService.createStripeForAuction(roundId, id, amount, paymentIntentId, paymentMethodId);
	}

	@PreAuthorize("isAuthenticated()")
	public String createCryptoPayment(int roundId, Double amount, String cryptoType, String network, String coin) throws ParseException, IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		CoinpaymentAuctionTransaction _m = new CoinpaymentAuctionTransaction(roundId, userId, amount, cryptoType, network, coin);
		return coinpaymentAuctionService.createNewTransaction(_m);
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
