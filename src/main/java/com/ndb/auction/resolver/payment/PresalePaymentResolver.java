package com.ndb.auction.resolver.payment;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.models.transactions.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.StripePresaleTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.apache.http.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresalePaymentResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public CoinpaymentPresaleTransaction createChargeForPresale(int presaleId, int orderId, Long amount, String coin, String network, String cryptoType, Double cryptoAmount) throws ParseException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        CoinpaymentPresaleTransaction m = new CoinpaymentPresaleTransaction(userId, presaleId, orderId, amount, coin, network, cryptoAmount, cryptoType);
        return (CoinpaymentPresaleTransaction) coinpaymentPresaleService.createNewTransaction(m);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentPresaleTransaction> getAllCryptoPresaleTx(String orderBy) {
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentPresaleTransaction> getCryptoPresaleTxByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public CoinpaymentPresaleTransaction getCryptoPresaleTxById(int id) {
        return (CoinpaymentPresaleTransaction) coinpaymentPresaleService.selectById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<CoinpaymentPresaleTransaction> getCryptoPresaleTxByAdmin(int userId, int presaleId) {
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.select(userId, presaleId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentPresaleTransaction> getCryptoPresaleTx(int presaleId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.select(userId, presaleId);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForPreSale(int presaleId, int orderId, Long amount, String paymentIntentId, String paymentMethodId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripePresaleTransaction m = new StripePresaleTransaction(userId, presaleId, orderId, amount, paymentIntentId, paymentMethodId);
        return stripePresaleService.createNewTransaction(m);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripePresaleTransaction> getStripePresaleTx(String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripePresaleTransaction> getStripePresaleTxByUser(int userId, String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public StripePresaleTransaction getStripePresaleTxById(int id) {
        return (StripePresaleTransaction) stripePresaleService.selectById(id);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripePresaleTransaction> getStripePresaleTxByPresaleId(int userId, int presaleId, String orderBy) {
        return (List<StripePresaleTransaction>) stripePresaleService.selectByPresale(userId, presaleId, orderBy);
    }

}
