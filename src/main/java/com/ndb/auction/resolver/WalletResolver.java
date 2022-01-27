package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.payload.Balance;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ndb.auction.service.user.UserDetailsImpl;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WalletResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    
    // get wallet balances 
    @PreAuthorize("isAuthenticated()")
    public List<Balance> getBalances() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return internalBalanceService.getInternalBalances(id);
    }

    // Add favorite token 
    public int addFavouriteToken(String token) {
        return 0;
    }
    
    // get deposit address 
    

}
