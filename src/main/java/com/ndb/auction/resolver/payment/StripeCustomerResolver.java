package com.ndb.auction.resolver.payment;

import java.util.List;

import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class StripeCustomerResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @PreAuthorize("isAuthenticated()")
    public List<StripeCustomer> getSavedCards() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return stripeCustomerService.getSavedCards(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public int deleteCard(int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        List<StripeCustomer> customerList = stripeCustomerService.getSavedCards(userId);
        for (StripeCustomer customer : customerList) {
            if(customer.getId() == id) {
                return stripeCustomerService.deleteStripeCustomer(id);
            }
        }
        return 0;
    }
}
