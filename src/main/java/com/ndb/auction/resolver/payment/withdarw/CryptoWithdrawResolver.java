package com.ndb.auction.resolver.payment.withdarw;

import java.util.List;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.service.withdraw.CryptoWithdrawService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class CryptoWithdrawResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @Autowired
	protected CryptoWithdrawService cryptoWithdrawService;

    @Autowired
    private TotpService totpService;

    @PreAuthorize("isAuthenticated()")
    public CryptoWithdraw cryptoWithdrawRequest(double amount, String sourceToken, String network, String des, String code) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var userEmail = userDetails.getEmail();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            throw new UnauthorizedException("Please verify your identity.", "userId");
        }

        // check withdraw code
        if(!totpService.checkWithdrawCode(userEmail, code)) {
            throw new BalanceException("2FA failed", "code");
        }

        // check source token balance
        double sourceBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        if(sourceBalance < amount) {
            throw new BalanceException("insufficient_balance", "withdrawAmount");
        }

        // get crypto price
        double cryptoPrice = 0.0;
        if(sourceToken.equals("USDT")) {
            cryptoPrice = 1.0;
        } else {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(sourceToken);
        }

        // double totalUSD = amount * cryptoPrice;
        double fee = getTierFee(userId, amount);
        double withdrawAmount = amount - fee;

        var m = new CryptoWithdraw(userId, withdrawAmount, fee, sourceToken, cryptoPrice, amount, network, des); 
        return (CryptoWithdraw) cryptoWithdrawService.createNewWithdrawRequest(m);
    }

    // confirm paypal withdraw
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int confirmCryptoWithdraw(int id, int status, String deniedReason) throws Exception {
        return cryptoWithdrawService.confirmWithdrawRequest(id, status, deniedReason);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByUser() {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByUserByAdmin(int userId) {
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByUser(userId);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByStatusByAdmin(int userId, int status) {
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByStatus(userId, status);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByStatus(int status) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByStatus(userId, status);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoPendingWithdrawRequests() {
        return (List<CryptoWithdraw>) cryptoWithdrawService.getAllPendingWithdrawRequests();
    }

    @PreAuthorize("isAuthenticated()")
    public CryptoWithdraw getCryptoWithdrawById(int id) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var m = (CryptoWithdraw) cryptoWithdrawService.getWithdrawRequestById(id);
        if(m.getUserId() != userId) return null;
        return m;
    }

    @PreAuthorize("isAuthenticated()")
    public CryptoWithdraw getCryptoWithdrawByIdByAdmin(int id) {
        return (CryptoWithdraw) cryptoWithdrawService.getWithdrawRequestById(id);
    }
}
