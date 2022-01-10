package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AvatarSet;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver  {
	
	// start time / duration / total amount / min price 
	// not sure => % of total amount for all rounds, previous min price!!
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Auction createAuction(
		int number, 
		String startedAt, 
		long duration, 
		long totalToken, 
		long minPrice, 
		List<AvatarSet> avatar, 
		long token
	) {
		Auction auction = new Auction(number, startedAt, duration, totalToken, minPrice, avatar, token);
		return auctionService.createNewAuction(auction);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<Auction> getAuctions() {
		return auctionService.getAuctionList();
	}

	@PreAuthorize("isAuthenticated()")
	public Auction getAuctionByNumber(int round) {
		return auctionService.getAuctionByRound(round);
	}

	@PreAuthorize("isAuthenticated()")
	public List<Auction> getAuctionByStatus(int status) {
		return auctionService.getAuctionByStatus(status);
	}
	
	@PreAuthorize("isAuthenticated()")
	public Auction getAuctionById(String id) {
		return auctionService.getAuctionById(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Auction updateAuction(
		int id, 
		int number, 
		long duration, 
		long totalToken, 
		long minPrice, 
		List<AvatarSet> avatarSet, 
		long token
	) {
		Auction auction = new Auction(number, null, duration, totalToken, minPrice, avatarSet, token);
		auction.setId(id);
		return auctionService.updateAuctionByAdmin(auction);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String checkRounds() {
		return auctionService.checkRounds();
	}
	
}
