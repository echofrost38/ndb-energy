package com.ndb.auction.resolver.payment.presale;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
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
    
    private static final String PRESALE = "PRESALE";
    
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public CoinpaymentDepositTransaction createChargeForPresale(int presaleId, int orderId, String coin, String network, String cryptoType) throws ParseException, IOException {
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
        CoinpaymentDepositTransaction m = new CoinpaymentDepositTransaction(
            orderId, 
            userId, 
            usdAmount, 
            _cryptoAmount,
            total- _cryptoAmount, 
            PRESALE, 
            cryptoType, 
            network, 
            coin);

        return coinpaymentPresaleService.createNewTransaction(m);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<CoinpaymentDepositTransaction> getAllCryptoPresaleTx() {
        return coinpaymentPresaleService.selectAll(PRESALE);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentDepositTransaction> getCryptoPresaleTxByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return coinpaymentPresaleService.selectByUser(userId, 1, PRESALE);
    }

    @PreAuthorize("isAuthenticated()")
    public CoinpaymentDepositTransaction getCryptoPresaleTxById(int id) {
        return coinpaymentPresaleService.selectById(id);
    }

}
