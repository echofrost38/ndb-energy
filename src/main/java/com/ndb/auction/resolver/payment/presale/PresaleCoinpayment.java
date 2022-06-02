package com.ndb.auction.resolver.payment.presale;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
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
    public CoinpaymentPresaleTransaction createChargeForPresale(int presaleId, int orderId, String coin, String network, String cryptoType) throws ParseException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var order = presaleOrderService.getPresaleById(orderId);

        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        var usdAmount = order.getNdbAmount() * order.getNdbPrice();
        var cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        var _cryptoAmount = usdAmount / cryptoPrice;

        double total = getTotalCoinpaymentOrder(userId, _cryptoAmount);
        // crypto amount means total order!!!!! including fee
        CoinpaymentPresaleTransaction m = new CoinpaymentPresaleTransaction(userId, presaleId, orderId, usdAmount, total-_cryptoAmount, coin, network, total, cryptoType);
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

    @PreAuthorize("hasRole('ROLE_SUPER')")
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
