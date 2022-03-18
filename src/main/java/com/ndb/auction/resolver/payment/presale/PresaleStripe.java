package com.ndb.auction.resolver.payment.presale;

import java.util.List;

import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleStripe extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForPreSale(
        int presaleId, 
        int orderId, 
        Long amount, 
        String paymentIntentId, 
        String paymentMethodId,
        boolean isSaveCard
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripePresaleTransaction m = new StripePresaleTransaction(userId, presaleId, orderId, amount, paymentIntentId, paymentMethodId);
        return stripePresaleService.createNewTransaction(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripePresaleTransaction> getStripePresaleTx(String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripePresaleTransaction> getStripePresaleTxByUser(int userId, String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public StripePresaleTransaction getStripePresaleTxById(int id) {
        return (StripePresaleTransaction) stripePresaleService.selectById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripePresaleTransaction> getStripePresaleTxByPresaleId(int userId, int presaleId, String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectByPresale(userId, presaleId, orderBy);
    }

}
