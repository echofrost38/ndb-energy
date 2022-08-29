package com.ndb.auction.resolver;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserSuspendedException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.service.user.UserDetailsImpl;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class BidResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	@PreAuthorize("isAuthenticated()")
	public Bid placeBid(
		int roundId, 
		double tokenAmount, 
		double tokenPrice
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
		if (userService.isUserSuspended(userId)) {
			throw new UserSuspendedException("User is suspended!");
		}

		// check KYC status!
		var kycStatus = shuftiService.kycStatusCkeck(userId);
		if (!kycStatus) {
			String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
			throw new UnauthorizedException(msg, "userId");
		}

		return bidService.placeNewBid(userId, roundId, tokenAmount, tokenPrice);
	}
	
	@PreAuthorize("isAuthenticated()")
	public Bid increaseBid(
		int roundId, 
		double tokenAmount, 
		double tokenPrice
		) {
			UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int id = userDetails.getId();
			return bidService.increaseBid(id, roundId, tokenAmount, tokenPrice);
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

	@PreAuthorize("isAuthenticated()")
	public List<Bid> getBidListFrom(Long from) {
		return bidService.getBidListFrom(from);
	}
	
	///////////////// for test 
	public String closeBid(int roundId) {
		bidService.closeBid(roundId);
		return "Success";
	}

	public int makeConfirmed(int userId, int roundId) {
		Bid bid = bidService.getBid(roundId, userId);
		bidService.updateBidRanking(bid);
		return 1;
	}

}
