package com.ndb.auction.resolver.payment.presale;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.models.transactions.coinpayment.CoinpaymentPresaleTransaction;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.expression.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleCoinpayment extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public CoinpaymentPresaleTransaction createChargeForPresale(int presaleId, int orderId, Long amount, String coin, String network, String cryptoType, Double cryptoAmount) throws ParseException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        CoinpaymentPresaleTransaction m = new CoinpaymentPresaleTransaction(userId, presaleId, orderId, amount, coin, network, cryptoAmount, cryptoType);
        return (CoinpaymentPresaleTransaction) coinpaymentPresaleService.createNewTransaction(m);
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentPresaleTransaction> getAllCryptoPresaleTx(String orderBy) {
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public List<CoinpaymentPresaleTransaction> getCryptoPresaleTxByAdmin(int userId, int presaleId) {
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.select(userId, presaleId);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<CoinpaymentPresaleTransaction> getCryptoPresaleTx(int presaleId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CoinpaymentPresaleTransaction>) coinpaymentPresaleService.select(userId, presaleId);
    }
}
