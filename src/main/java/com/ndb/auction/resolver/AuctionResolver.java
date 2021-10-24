package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver  {
	
	public Auction createAuction(int number, String startedAt, double totalToken, double minPrice) {
		Auction auction = new Auction(number, startedAt, totalToken, minPrice);
		return auctionService.createNewAuction(auction);
	}
	
	public List<Auction> getAuctions() {
		return auctionService.getAuctionList();
	}
	
	public Auction getAuctionById(String id) {
		return auctionService.getAuctionById(id);
	}
	
	public Auction updateAuction(String id, int number, String startedAt, double totalToken, double minPrice) {
		Auction auction = new Auction(number, startedAt, totalToken, minPrice);
		auction.setAuctionId(id);
		return auctionService.updateAuctionByAdmin(auction);
	}
}
