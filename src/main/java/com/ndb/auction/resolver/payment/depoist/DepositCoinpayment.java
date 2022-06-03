package com.ndb.auction.resolver.payment.depoist;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.apache.http.client.ClientProtocolException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositCoinpayment extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    // get deposit address 
    @PreAuthorize("isAuthenticated()")
    public CoinpaymentWalletTransaction createChargeForDeposit(String coin, String network, String cryptoType) throws ClientProtocolException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        CoinpaymentWalletTransaction m = new CoinpaymentWalletTransaction(userId, 0.0, 0.0, coin, network, 0.0, cryptoType);
        return (CoinpaymentWalletTransaction) coinpaymentWalletService.createNewTransaction(m);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<CoinpaymentWalletTransaction> getCoinpaymentDepositTx(String orderBy) {
        return (List<CoinpaymentWalletTransaction>) coinpaymentWalletService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<CoinpaymentWalletTransaction> getCoinpaymentDepositTxByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CoinpaymentWalletTransaction>) coinpaymentWalletService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<CoinpaymentWalletTransaction> getCoinpaymentDepositTxByAdmin(int userId, String orderBy) {
        return (List<CoinpaymentWalletTransaction>) coinpaymentWalletService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public CoinpaymentWalletTransaction getCoinpaymentDepositTxById(int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var m = (CoinpaymentWalletTransaction) coinpaymentWalletService.selectById(id);
        if(m.getUserId() != userId) {
            throw new UnauthorizedException("You have no permission.", "id");
        }
        return m;
    }
}
