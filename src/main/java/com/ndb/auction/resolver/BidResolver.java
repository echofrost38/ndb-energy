package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.Bid;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BidResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	public Bid placeBid(String userId, String roundId, Double tokenAmount, Double tokenPrice, Integer payment) {
		return bidService.placeNewBid(userId, roundId, tokenAmount, tokenPrice, payment);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<Bid> getBidListByRound(Integer round){
		return bidService.getBidListByRound(round);
	}
	
	public List<Bid> getBidListByUser(String userId) {
		return bidService.getBidListByUser(userId);
	}
	
	public Bid getBid(String userId, Integer round) {
		return bidService.getBid(round, userId);
	}
	
	public Bid updateBid(String userId, String roundId, Double tokenAmount, Double tokenPrice) {
		return bidService.updateBid(userId, roundId, tokenAmount, tokenPrice);
	}
}
