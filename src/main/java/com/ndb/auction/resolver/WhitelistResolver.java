package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.user.Whitelist;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.WhitelistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WhitelistResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @Autowired
    private WhitelistService whitelistService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int putInWhitelist(int userId, String reason) {
        return whitelistService.insert(userId, reason);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Whitelist> getWhitelists() {
        return whitelistService.selectAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Whitelist getWhitelistByUser(int userId) {
        return whitelistService.selectByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int removeFromWhitelist(int userId) {
        return whitelistService.delete(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public Whitelist checkWhitelist() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return whitelistService.selectByUser(userId);
    }
}
