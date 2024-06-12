package com.ndb.auction.resolver.payment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class WalletResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    
    // get wallet balances 
    @PreAuthorize("isAuthenticated()")
    public List<BalancePayload> getBalances() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return internalBalanceService.getInternalBalances(id);
    }

    // Add favorite token 
    public int addFavouriteToken(String token) {
        return 0;
    }
    
    
    
    // Deposit with Plaid.com
    @PreAuthorize("isAuthenticated()")
    public String depositWithPlaid() throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return plaidService.createLinkToken(userId).getLinkToken();
    }

    @PreAuthorize("isAuthenticated()")
    public String plaidExchangeToken(String publicToken) throws IOException {
        return plaidService.getExchangeToken(publicToken).getAccessToken();
    }

    @PreAuthorize("isAuthenticated()")
    public boolean withdrawCrypto(String to, double amount, String tokenSymbol) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // 1) check balance 
        CryptoBalance balance = internalBalanceService.getBalance(userId, tokenSymbol);
        if(balance.getFree() < amount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "amount");
        }

        // 2) check KYC level
        KYCSetting kyc = baseVerifyService.getKYCSetting("KYC");
        
        if(kyc == null) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            new BalanceException(msg, "kind");
        }

        if(kyc.getWithdraw() <= amount) {
            if(!shuftiService.kycStatusCkeck(userId)) {
                String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
                throw new BalanceException(msg, "kind");
            }
        }

        // withdraw of NDB
        if(ndbCoinService.transferNDB(userId, to, amount)) {
            // deduct from wallet
            internalBalanceService.deductFree(userId, tokenSymbol, amount);
        } else {
            return false;
        }

        return true;
    }

}
