package com.ndb.auction.resolver;

import org.springframework.stereotype.Component;

import java.util.List;

import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.FiatTransaction;
import com.ndb.auction.payload.PayResponse;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	// for stripe payment
	public String getStripePubKey() {
		return stripeService.getPublicKey();
	}
	
	public PayResponse stripePayment(String roundId, String userId, Long amount, String paymentIntentId) {
		return stripeService.createNewPayment(roundId, userId, amount, paymentIntentId);
	}

	public List<FiatTransaction> getTransactionsByRound(String roundId) {
		return stripeService.getTransactionsByRound(roundId);
	}

	public List<FiatTransaction> getTransactionsByUser(String userId) {
		return stripeService.getTransactionByUser(userId);
	}

	public FiatTransaction getFiatTransaction(String roundId, String userId) {
		return stripeService.getTransaction(roundId, userId);
	}


	// for crypto payment
	public CryptoTransaction createCryptoPayment(String roundId, String userId, Double amount) {
		return cryptoService.createNewPayment(roundId, userId, amount);
	}

	public CryptoTransaction getCryptoTransactionByCode(String code) {
		return cryptoService.getTransactionById(code);
	}

	public List<CryptoTransaction> getTransactionByUser(String userId) {
		return cryptoService.getTransactionByUser(userId);
	}

	public List<CryptoTransaction> getTransactionByRound(String roundId) {
		return cryptoService.getTransactionByRound(roundId);
	}
	
	public CryptoTransaction getCryptoTransaction(String roundId, String userId) {
		return cryptoService.getTransaction(roundId, userId);
	}

}
