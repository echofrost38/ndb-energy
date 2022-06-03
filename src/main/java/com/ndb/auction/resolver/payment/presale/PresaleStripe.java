package com.ndb.auction.resolver.payment.presale;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
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
    public PayResponse payStripeForPreSale(int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // getting value from presale order
        var order = presaleOrderService.getPresaleById(orderId);
        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }
        var usdAmount = order.getNdbAmount() * order.getNdbPrice();
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = usdAmount * fiatPrice;
        StripePresaleTransaction m = new StripePresaleTransaction(userId, presaleId, orderId, usdAmount, _fiatAmount, fiatType, paymentIntentId, paymentMethodId);
        return stripePresaleService.createNewTransaction(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForPreSaleWithSavedCard(int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if(userId != customer.getUserId()){
            String msg = messageSource.getMessage("failed_auth_card", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg,"USER_ID");
        }

        // getting value from presale order
        var order = presaleOrderService.getPresaleById(orderId);
        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }
        var usdAmount = order.getNdbAmount() * order.getNdbPrice();
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = usdAmount * fiatPrice;
        StripePresaleTransaction m = new StripePresaleTransaction(userId, presaleId, orderId, usdAmount, _fiatAmount, fiatType, paymentIntentId);
        return stripePresaleService.createNewTransactionWithSavedCard(m, customer);
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
