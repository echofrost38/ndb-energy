package com.ndb.auction.resolver;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BidResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	@PreAuthorize("isAuthenticated()")
	public Bid placeBid(
		int roundId, 
		long tokenAmount, 
		long tokenPrice, 
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
	public Bid updateBid(
		int roundId, 
		long tokenAmount, 
		long tokenPrice
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int id = userDetails.getId();
		// if(!sumsubService.checkThreshold(id, "bid", tokenAmount * tokenPrice)) {
		// 	throw new UnauthorizedException("Please verify your identification", "tokenAmount");
		// }
		return bidService.updateBid(id, roundId, tokenAmount, tokenPrice);
	}
	
	@PreAuthorize("isAuthenticated()")
	public Bid increaseBid(
		int roundId, 
		long tokenAmount, 
		long tokenPrice, 
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
	public Bid getBidByAdmin(int userId, Integer round) {
		return bidService.getBid(round, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public Bid getBid(Integer round) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return bidService.getBid(round, id);
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
