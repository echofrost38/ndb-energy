package com.ndb.auction.resolver;

import org.springframework.stereotype.Component;

import com.ndb.auction.payload.PayResponse;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	public String getStripePubKey() {
		return stripeService.getPublicKey();
	}
	
	public PayResponse stripePayment(String roundId, String userId, Long amount, String paymentIntentId) {
		return stripeService.createNewPayment(roundId, userId, amount, paymentIntentId);
	}
}
