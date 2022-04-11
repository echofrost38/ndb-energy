package com.ndb.auction.resolver.payment.withdarw;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.service.withdraw.BankWithdrawService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BankWithdrawResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @Autowired
    private BankWithdrawService bankWithdrawService;

    @Autowired
    private TotpService totpService;

    @PreAuthorize("isAuthenticated()")
    public BankWithdrawRequest bankWithdrawRequest(
        String targetCurrency,
        double amount, // requested amount in USD!
        String sourceToken,
        int mode,
        String country, // ignore for international 
        String holderName,
        String bankName,
        String accNumber,
        String metadata,
        String code
    ) {
        // check user and kyc status
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

        // get token price
        double tokenPrice = thirdAPIUtils.getCryptoPriceBySymbol(sourceToken);

        // get token balance
        double tokenBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        double usdBalance = tokenBalance * tokenPrice;

        // checking balance 
        if(usdBalance < amount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "amount");
        }

        // get fee in usd
        var fee = getTierFee(userId, amount); 
        var withdrawAmount = amount - fee;
        var tokenAmount = amount / tokenPrice;

        var m = new BankWithdrawRequest(
            userId, targetCurrency, withdrawAmount, fee, sourceToken, tokenPrice, tokenAmount,
            mode, country, holderName, bankName, accNumber, metadata
        );
        bankWithdrawService.createNewRequest(m);
        return m;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankWithdrawRequest> getPendingBankWithdrawRequests() {
        return bankWithdrawService.getAllPendingRequests();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankWithdrawRequest> getAllApprovedBankWithdrawRequests() {
        return bankWithdrawService.getAllApproved();
    } 

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankWithdrawRequest> getAllDeniedBankWithdrawRequests() {
        return bankWithdrawService.getAllDenied();
    }

    @PreAuthorize("isAuthenticated()")
    public List<BankWithdrawRequest> getBankWithdrawRequests() {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return bankWithdrawService.getRequestsByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BankWithdrawRequest getBankWithdrawRequest(int id) {
        return bankWithdrawService.getRequestById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int approveBankWithdrawRequest(int id) {
        return bankWithdrawService.approveRequest(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int denyBankWithdrawRequest(int id, String reason) {
        return bankWithdrawService.denyRequest(id, reason);
    }

}
