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

    public Boolean transferFunds(String token, String network, String address, long amount) {
        return ndbWalletService.transferFunds(token, network, address, amount);
    }
}
