package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver  {
	
	// start time / duration / total amount / min price 
	// not sure => % of total amount for all rounds, previous min price!!
	public Auction createAuction(int number, String startedAt, long duration, double totalToken, double minPrice) {
		Auction auction = new Auction(number, startedAt, duration, totalToken, minPrice);
		return auctionService.createNewAuction(auction);
	}
	
	public List<Auction> getAuctions() {
		return auctionService.getAuctionList();
	}
	
	public Auction getAuctionById(String id) {
		return auctionService.getAuctionById(id);
	}
	
	public Auction updateAuction(String id, int number, String startedAt, long duration, double totalToken, double minPrice) {
		Auction auction = new Auction(number, startedAt, duration, totalToken, minPrice);
		auction.setAuctionId(id);
		return auctionService.updateAuctionByAdmin(auction);
	}
	
}
