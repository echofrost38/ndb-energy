package com.ndb.auction.resolver.payment.deposit;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.payment.stripe.StripeDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    private final StripeDepositService stripeDepositService;
	private final StripeCustomerService stripeCustomerService;

    @Autowired
    public DepositStripe(StripeDepositService stripeDepositService, StripeCustomerService stripeCustomerService) {
        this.stripeDepositService = stripeDepositService;
        this.stripeCustomerService = stripeCustomerService;
    }

    // Deposit with Stripe
    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDeposit(Double amount, Double fiatAmount, String fiatType, String cryptoType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        StripeDepositTransaction m = new StripeDepositTransaction(userId, amount, fiatAmount, fiatType, cryptoType, paymentIntentId, paymentMethodId);
        return stripeDepositService.createDeposit(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDepositWithSavedCard(Double amount, Double fiatAmount, String fiatType, String cryptoType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if(userId != customer.getUserId()){
            String msg = messageSource.getMessage("failed_auth_card", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg,"USER_ID");
        }
        StripeDepositTransaction m = new StripeDepositTransaction(userId, amount, fiatAmount, fiatType, cryptoType, paymentIntentId);
        return stripeDepositService.createDepositWithSavedCard(m, customer);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTx(String orderBy) {
        return (List<StripeDepositTransaction>) stripeDepositService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTxByUser(String orderBy, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<StripeDepositTransaction>) stripeDepositService.selectByUser(userId, orderBy, showStatus);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<StripeDepositTransaction> getStripeDepositTxByAdmin(int userId, String orderBy) {
        // admin will get all transactions by default
        return (List<StripeDepositTransaction>) stripeDepositService.selectByUser(userId, orderBy, 1);
    }

    @PreAuthorize("isAuthenticated()")
    public StripeDepositTransaction getStripeDepositTxById(int id, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var tx = (StripeDepositTransaction) stripeDepositService.selectById(id, showStatus);
        if(tx.getUserId() == userId) {
            return tx;
        }
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    public int changeStripeDepositShowStatus(int id, int showStatus) {
        return stripeDepositService.changeShowStatus(id, showStatus);
    }
}
