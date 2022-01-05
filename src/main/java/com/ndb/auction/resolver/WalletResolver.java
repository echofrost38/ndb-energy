package com.ndb.auction.resolver;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WalletResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    // get deposit address 


    // Testing purpose 
    public String getInternalAddress(String id, String token, String network) {
        return ndbWalletService.generateWalletAddress(id, token);
    }

    public String getWalletBalance(String token, String network, String address) {
        return ndbWalletService.getWalletBalance(token, network, address).toString();
    }

    public Boolean transferFunds(String token, String network, String address, int amount) {
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
