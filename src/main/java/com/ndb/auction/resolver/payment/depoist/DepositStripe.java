package com.ndb.auction.resolver.payment.depoist;

import java.util.List;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    private final StripeDepositService stripeDepositService;

    @Autowired
    public DepositStripe(StripeDepositService stripeDepositService) {
        this.stripeDepositService = stripeDepositService;
    }

    // Deposit with Stripe
    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDeposit(Long amount, String cryptoType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeDepositTransaction m = new StripeDepositTransaction(userId, amount, cryptoType, paymentIntentId, paymentMethodId);
        return stripeDepositService.createDeposit(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDepositWithSavedCard(Long amount, String cryptoType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if(userId != customer.getUserId()){
            throw new UnauthorizedException("The user is not authorized to use this card.","USER_ID");
        }
        StripeDepositTransaction m = new StripeDepositTransaction(userId, amount, cryptoType, paymentIntentId);
        return stripeDepositService.createDepositWithSavedCard(m, customer);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTx(String orderBy) {
        return (List<StripeDepositTransaction>) stripeDepositService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTxByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<StripeDepositTransaction>) stripeDepositService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTxByAdmin(int userId, String orderBy) {
        return (List<StripeDepositTransaction>) stripeDepositService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public StripeDepositTransaction getStripeDepositTxById(int id) {
        return (StripeDepositTransaction) stripeDepositService.selectById(id);
    }
}
