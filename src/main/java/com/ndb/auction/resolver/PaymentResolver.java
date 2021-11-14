package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.payload.PayResponse;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	// for stripe payment
	@PreAuthorize("isAuthenticated()")
	public String getStripePubKey() {
		return stripeService.getPublicKey();
	}
	
	@PreAuthorize("isAuthenticated()")
	public PayResponse stripePayment(
		String roundId, 
		String userId, 
		Long amount, 
		String paymentIntentId 
	) {
		return stripeService.createNewPayment(roundId, userId, amount, paymentIntentId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionsByRound(String roundId) {
		return stripeService.getTransactionsByRound(roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeTransaction> getStripeTransactionsByUser(String userId) {
		return stripeService.getTransactionByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransaction(String roundId, String userId) {
		return stripeService.getTransactions(roundId, userId);
	}


	// for crypto payment
	@PreAuthorize("isAuthenticated()")
	public CryptoTransaction createCryptoPayment(
		String roundId, 
		String userId, 
		Double amount
	) {
		return cryptoService.createNewPayment(roundId, userId, amount);
	}


	public CryptoTransaction getCryptoTransactionByCode(String code) {
		return cryptoService.getTransactionById(code);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CryptoTransaction> getCryptoTransactionByUser(String userId) {
		return cryptoService.getTransactionByUser(userId);
	}

	public List<CryptoTransaction> getCryptoTransactionByRound(String roundId) {
		return cryptoService.getTransactionByRound(roundId);
	}
	
	public List<CryptoTransaction> getCryptoTransaction(String roundId, String userId) {
		return cryptoService.getTransaction(roundId, userId);
	}

	public List<Coin> getCoins() {
		return cryptoService.getCoinList();
	}

	// admin 
	public Coin addNewCoin(String name, String symbol) {
		return cryptoService.addNewCoin(name, symbol);
	}

	public Coin deleteCoin(String name, String symbol) {
		return cryptoService.deleteCoin(name, symbol);
	}



}
