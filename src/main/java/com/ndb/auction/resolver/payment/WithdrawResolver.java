package com.ndb.auction.resolver.payment;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

/**
 * User send withdraw request to admin
 * then admin check withdraw request and permit. 
 * 
 * withdraw condition 
 * 1. Enough balance
 * 2. KYC checking 
 * 
 * Withdraw source are crypto assets!
 * 
 */

@Component
public class WithdrawResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // Create crypto withdraw request!
    @PreAuthorize("isAuthenticated()")
    public boolean cryptoWithdrawRequest(String to, double amount, String token) {
        

        return false;
    }

    // Create paypal withdraw request!
    @PreAuthorize("isAuthenticated()")
    public PaypalWithdraw paypalWithdrawRequest(String email, double withdrawAmount, String sourceToken) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // check source token balance
        double sourceBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        if(sourceBalance < withdrawAmount) {
            throw new BalanceException("insufficient_balance", "withdrawAmount");
        }
        
        // KYC withdraw limit
        var kycSetting = baseVerifyService.getKYCSetting("KYC");

        // KYC check
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            // throw new UnauthorizedException("no_kyc", "userId");
        }

        // send request
        var m = new PaypalWithdraw();
        return (PaypalWithdraw) paypalWithdrawService.createNewWithdrawRequest(m);
    }

    // confirm paypal withdraw
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int confirmPaypalWithdraw(int id, int status, String deniedReason) throws Exception {
        return paypalWithdrawService.confirmWithdrawRequest(id, status, deniedReason);
    }
    
    // Create bank withdraw request!
    @PreAuthorize("isAuthenticated()")
    public boolean bankWithdrawRequest(String email, double amount, String token) {


        return false;
    }

}
