package com.ndb.auction.resolver;

import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
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
    public boolean changeReferralCommissionWallet(String wallet) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        kycStatus=true; //only debug
        if (kycStatus)
            return referralService.updateReferrerAddress(userDetails.getId(),wallet);
        else
            return false;
    }

    @PreAuthorize("isAuthenticated()")
    public String activateReferralCode(String wallet) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        kycStatus=true ; //only debug
        if (kycStatus){
            UserReferral referrer = referralService.createNewReferrer(userId,wallet,"");
            return referrer.getReferralCode();
        } else
            return "";
    }

    @PreAuthorize("isAuthenticated()")
    public UserReferral getReferral() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if (kycStatus){
            UserReferral referral = referralService.selectById(userId);
            return referralService.selectById(userId);
        }
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    public List<UserReferralEarning> getReferredUsers() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return referralService.earningByReferrer(userId);
    }
}
