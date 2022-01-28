package com.ndb.auction.resolver.payment;

import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.resolver.BaseResolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresalePaymentResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public CryptoPayload createChargeForPresale(int orderId) {
        return presaleOrderService.payOrderWithCrypto(orderId);
    }

}
