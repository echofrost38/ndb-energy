package com.ndb.auction.resolver;

import com.ndb.auction.models.referral.UpdateReferralAddressResponse;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.UserSocialService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSocialResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    UserSocialService socialService;

    @PreAuthorize("isAuthenticated()")
    public Boolean addDiscord(String username) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return  socialService.addDiscord(userDetails.getId(),username);
    }
}