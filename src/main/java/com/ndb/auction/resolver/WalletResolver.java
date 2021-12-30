package com.ndb.auction.resolver;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WalletResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    // get deposit address 


    // Testing purpose 
    public String getDepositAddress(String id, String token, String network) {
        return ndbWalletService.generateWalletAddress(id, token);
    }
}
