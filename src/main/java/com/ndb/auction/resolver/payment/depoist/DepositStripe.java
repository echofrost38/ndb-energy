package com.ndb.auction.resolver.payment.depoist;

import java.util.List;

import com.ndb.auction.models.transactions.stripe.StripeWalletTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    // Deposit with Stripe
    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForDeposit(Long amount, String currencyName, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeWalletTransaction m = new StripeWalletTransaction(userId, amount, paymentIntentId, paymentMethodId);
        return stripeWalletService.createNewTransaction(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForDepositWithSavedCard(Long amount, String currencyName, String paymentIntentId, String paymentMethodId, int cardId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        String customerId = stripeCustomerService.getSavedCard(cardId).getCustomerId();
        StripeWalletTransaction m = new StripeWalletTransaction(userId, amount, paymentIntentId, paymentMethodId);
        return stripeWalletService.createTransactionWithSavedCard(m, customerId);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeWalletTransaction> getStripeDepositTx(String orderBy) {
        return (List<StripeWalletTransaction>) stripeWalletService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeWalletTransaction> getStripeDepositTxByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<StripeWalletTransaction>) stripeWalletService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<StripeWalletTransaction> getStripeDepositTxByAdmin(int userId, String orderBy) {
        return (List<StripeWalletTransaction>) stripeWalletService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public StripeWalletTransaction getStripeDepositTxById(int id) {
        return (StripeWalletTransaction) stripeWalletService.selectById(id);
    }
}
