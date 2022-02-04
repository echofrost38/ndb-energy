package com.ndb.auction.resolver.payment;

import java.io.IOException;

import com.ndb.auction.resolver.BaseResolver;

import org.apache.http.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresalePaymentResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public String createChargeForPresale(int orderId, Double amount, String currency) throws ParseException, IOException {
        return presaleOrderService.payOrderWithCrypto(orderId, amount, currency);
    }

}
