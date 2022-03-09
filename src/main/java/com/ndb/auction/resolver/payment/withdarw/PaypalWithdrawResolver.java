package com.ndb.auction.resolver.payment.withdarw;

import java.util.List;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PaypalWithdrawResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // Create paypal withdraw request!
    /**
     * 
     * @param email receiver email address
     * @param target target currency
     * @param amount crypto amount to withdraw
     * @param sourceToken crypto token to withdraw
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public PaypalWithdraw paypalWithdrawRequest(String email, String target, double amount, String sourceToken) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // check source token balance
        double sourceBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        if(sourceBalance < amount) {
            throw new BalanceException("insufficient_balance", "withdrawAmount");
        }

        // KYC withdraw limit
        var kycSetting = baseVerifyService.getKYCSetting("KYC");

        // KYC check
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            // throw new UnauthorizedException("no_kyc", "userId");
        }
        
        // get crypto price
        double cryptoPrice = 0.0;
        if(sourceToken.equals("USDT")) {
            cryptoPrice = 1.0;
        } else {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(sourceToken);
        }

        double totalUSD = amount * cryptoPrice;
        double fee = getPaypalWithdrawFee(userId, totalUSD);
        double withdrawAmount = totalUSD - fee;

        // send request
        var m = new PaypalWithdraw(userId, target, withdrawAmount, fee, sourceToken, cryptoPrice, amount, null, null, email);
        return (PaypalWithdraw) paypalWithdrawService.createNewWithdrawRequest(m);
    }

    // confirm paypal withdraw
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int confirmPaypalWithdraw(int id, int status, String deniedReason) throws Exception {
        return paypalWithdrawService.confirmWithdrawRequest(id, status, deniedReason);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalWithdrawByUser(int userId) {
        return (List<PaypalWithdraw>) paypalWithdrawService.getWithdrawRequestByUser(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalWithdrawByStatus(int userId, int status) {
        return (List<PaypalWithdraw>) paypalWithdrawService.getWithdrawRequestByStatus(userId, status);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalPendingWithdrawRequests() {
        return (List<PaypalWithdraw>) paypalWithdrawService.getAllPendingWithdrawRequests();
    }

    @PreAuthorize("isAuthenticated()")
    public PaypalWithdraw getPaypalWithdrawById(int id) {
        return (PaypalWithdraw) paypalWithdrawService.getWithdrawRequestById(id);
    }



}