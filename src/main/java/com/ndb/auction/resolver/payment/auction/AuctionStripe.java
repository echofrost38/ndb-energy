package com.ndb.auction.resolver.payment.auction;

import java.util.List;

import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
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
		StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, amount, paymentIntentId, paymentMethodId);
		return stripeAuctionService.createNewTransaction(m, isSaveCard);
	}
}
