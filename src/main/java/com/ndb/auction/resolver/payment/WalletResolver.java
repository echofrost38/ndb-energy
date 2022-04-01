package com.ndb.auction.resolver.payment;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.resolver.BaseResolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ndb.auction.service.user.UserDetailsImpl;

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
            throw new BalanceException("no_enough_fund", "amount");
        }

        // 2) check KYC level
        KYCSetting kyc = baseVerifyService.getKYCSetting("KYC");
        
        if(kyc == null) {
            throw new BalanceException("Please verify your identity.", "kind");
        }

        if(kyc.getWithdraw() <= amount) {
            if(!shuftiService.kycStatusCkeck(userId)) {
                throw new BalanceException("Please verify your identity._verified", "kind");
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
