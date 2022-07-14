package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.referral.ActiveReferralResponse;
import com.ndb.auction.models.referral.UpdateReferralAddressResponse;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.models.user.UserReferralEarning;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class ReferralResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    @PreAuthorize("isAuthenticated()")
    public UpdateReferralAddressResponse changeReferralCommissionWallet(String wallet) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return referralService.updateReferrerAddress(userDetails.getId(), wallet);
    }

    @PreAuthorize("isAuthenticated()")
    public ActiveReferralResponse activateReferralCode(String wallet) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return referralService.activateReferralCode(userId, wallet);
    }

    @PreAuthorize("isAuthenticated()")
    public UserReferral getReferral() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return referralService.selectById(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public int checkTimeLock() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return  referralService.checkTimeLock(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<UserReferralEarning> getReferredUsers() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return referralService.earningByReferrer(userId);
    }
}
