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


    // Testing purpose 
    public String getInternalAddress(String id, String token, String network) {
        return ndbWalletService.generateWalletAddress(id, token);
    }

    public String getWalletBalance(String token, String network, String address) {
        return ndbWalletService.getWalletBalance(token, network, address).toString();
    }

    public Boolean transferFunds(String token, String network, String address, long amount) {
        return ndbWalletService.transferFunds(token, network, address, amount);
    }

    public Boolean makeAllowance(String token, String network, String address, int amount) {
        return ndbWalletService.makeAllowance(token, network, address, amount);
    }

    public Boolean transferFromFunds(String token, String network, String from, String to, int amount) {
        return ndbWalletService.transferFromFunds(token, network, from, to, amount);
    }

    public String getAllowance(String token, String network, String owner, String spender) {
        return ndbWalletService.getAllowance(token, network, owner, spender);
    }

}
