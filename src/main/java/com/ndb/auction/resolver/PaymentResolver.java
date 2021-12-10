package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.payload.PayResponse;
import com.ndb.auction.service.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component

public class PaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	// for stripe payment
//	@PreAuthorize("isAuthenticated()")
	@CrossOrigin
	public String getStripePubKey() {
		return stripeService.getPublicKey();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionsByRound(String roundId) {
		return stripeService.getTransactionsByRound(roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeTransaction> getStripeTransactionsByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();
		return stripeService.getTransactionByUser(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionsByAdmin(String userId) {
		return stripeService.getTransactionByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeTransaction> getStripeTransactionByAdmin(String roundId, String userId) {
		return stripeService.getTransactions(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeTransaction> getStripeTransaction(String roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();
		return stripeService.getTransactions(roundId, id);
	}


	@PreAuthorize("isAuthenticated()")
	public PayResponse stripePayment(
		String roundId, 
		Long amount, 
		String paymentIntentId,
		String paymentMethodId
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();
		return stripeService.createNewPayment(roundId, id, amount, paymentIntentId, paymentMethodId);
	}


	@PreAuthorize("isAuthenticated()")
	public CryptoPayload createCryptoPayment(
		String roundId, 
		Double amount
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
		return cryptoService.createNewPayment(roundId, userId, amount);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public CryptoTransaction getCryptoTransactionByCode(String code) {
		return cryptoService.getTransactionById(code);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactionByAdmin(String userId) {
		return cryptoService.getTransactionByUser(userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CryptoTransaction> getCryptoTransactionByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
		return cryptoService.getTransactionByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactionByRound(String roundId) {
		return cryptoService.getTransactionByRound(roundId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CryptoTransaction> getCryptoTransactions(String roundId, String userId) {
		return cryptoService.getTransaction(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CryptoTransaction> getCryptoTransaction(String roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
		return cryptoService.getTransaction(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<Coin> getCoins() {
		return cryptoService.getCoinList();
	}

	@PreAuthorize("isAuthenticated()")
	public double getCryptoPrice(String symbol) {
		return cryptoService.getCryptoPriceBySymbol(symbol);
	}

	// admin 
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public Coin addNewCoin(String name, String symbol) {
		return cryptoService.addNewCoin(name, symbol);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Coin deleteCoin(String name, String symbol) {
		return cryptoService.deleteCoin(name, symbol);
	}



}
