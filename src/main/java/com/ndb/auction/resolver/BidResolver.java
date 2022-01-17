package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.Bid;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BidResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	@PreAuthorize("isAuthenticated()")
	public Bid placeBid(
		int roundId, 
		Long tokenAmount, 
		Long tokenPrice, 
		int payment, 
		String cryptoType
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
		// if(!sumsubService.checkThreshold(userId, "bid", tokenAmount * tokenPrice)) {
		// 	throw new UnauthorizedException("Please verify your identification", "tokenAmount");
		// }
		return bidService.placeNewBid(userId, roundId, tokenAmount, tokenPrice, payment, cryptoType);
	}
	
	@PreAuthorize("isAuthenticated()")
	public Bid increaseBid(
		int roundId, 
		Long tokenAmount, 
		Long tokenPrice, 
		Integer payment, 
		String cryptoType
		) {
			UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int id = userDetails.getId();
			return bidService.increaseBid(id, roundId, tokenAmount, tokenPrice, payment, cryptoType);
		}
		
	@PreAuthorize("isAuthenticated()")
	public List<Bid> getBidListByRound(Integer round){
		return bidService.getBidListByRound(round);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<Bid> getBidListByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return bidService.getBidListByUser(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Bid> getBidListByAdmin(int userId) {
		return bidService.getBidListByUser(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Bid getBidByAdmin(int userId, Integer roundId) {
		return bidService.getBid(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public Bid getBid(Integer roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return bidService.getBid(roundId, id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Bid> getBidListById(int roundId) {
		return bidService.getBidListByRoundId(roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<Bid> getBidList() {
		return bidService.getBidList();
	}
	
	///////////////// for test 
	public String closeBid(int roundId) {
		bidService.closeBid(roundId);
		return "Success";
	}
}
