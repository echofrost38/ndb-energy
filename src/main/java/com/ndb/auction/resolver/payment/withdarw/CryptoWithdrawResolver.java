package com.ndb.auction.resolver.payment.withdarw;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.Notification;
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
    public CryptoWithdraw cryptoWithdrawRequest(
        double amount, 
        String sourceToken, 
        String network, 
        String des, 
        String code
    ) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var userEmail = userDetails.getEmail();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        // check withdraw code
        if(!totpService.checkWithdrawCode(userEmail, code)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new BalanceException(msg, "code");
        }

        // check source token balance
        double sourceBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        if(sourceBalance < amount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "amount");
        }

        // get crypto price
        double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(sourceToken);;

        // double totalUSD = amount * cryptoPrice;
        double fee = getTierFee(userId, amount);
        double withdrawAmount = amount - fee;

        var m = new CryptoWithdraw(userId, withdrawAmount, fee, sourceToken, cryptoPrice, amount, network, des); 
        return (CryptoWithdraw) cryptoWithdrawService.createNewWithdrawRequest(m);
    }

    // confirm paypal withdraw
    @PreAuthorize("hasRole('ROLE_SUPER')")
    public int confirmCryptoWithdraw(int id, int status, String deniedReason) throws Exception {
        var result = cryptoWithdrawService.confirmWithdrawRequest(id, status, deniedReason);
        var request = cryptoWithdrawService.getWithdrawRequestById(id);
        var tokenSymbol = request.getSourceToken();
        var tokenAmount = request.getTokenAmount();

        if(result == 1 && status == 1) {
            // success
            internalBalanceService.deductFree(request.getUserId(), tokenSymbol, tokenAmount);
            notificationService.sendNotification(
                request.getUserId(),
                Notification.PAYMENT_RESULT,
                "PAYMENT CONFIRMED",
                String.format("Your %f %s withdarwal request has been approved", tokenAmount, tokenSymbol));
            return result;    
        } else if(status != 1) {
            notificationService.sendNotification(
                    request.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    String.format("Your %f %s withdarwal request has been denied", tokenAmount, tokenSymbol));
        }

        return result;
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByUser() {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getCryptoWithdrawByUserByAdmin(int userId) {
        return (List<CryptoWithdraw>) cryptoWithdrawService.getWithdrawRequestByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<CryptoWithdraw> getAllCryptoWithdraws() {
        return (List<CryptoWithdraw>) cryptoWithdrawService.getAllWithdrawRequests();
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
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

    @PreAuthorize("hasRole('ROLE_SUPER')")
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
