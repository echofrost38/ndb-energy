package com.ndb.auction.resolver.payment;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.payload.BalancePerUser;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WalletResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    // get wallet balances 
    @PreAuthorize("isAuthenticated()")
    public List<BalancePayload> getBalances() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return internalBalanceService.getInternalBalances(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BalancePayload> getBalancesByUserIdByAdmin(int userId) {
        return internalBalanceService.getInternalBalances(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BalancePerUser> getBalancesByAdmin() {
        return internalBalanceService.getInternalBalancesAllUsers();
    }

    // Add favorite token 
    public int addFavouriteToken(String token) {
        return 0;
    }

    // Deposit with Plaid.com
    @PreAuthorize("isAuthenticated()")
    public String depositWithPlaid() throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return plaidService.createLinkToken(userId).getLinkToken();
    }

    @PreAuthorize("isAuthenticated()")
    public String plaidExchangeToken(String publicToken) throws IOException {
        return plaidService.getExchangeToken(publicToken).getAccessToken();
    }

}
