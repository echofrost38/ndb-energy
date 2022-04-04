package com.ndb.auction.resolver.payment.depoist;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.bank.BankDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositBank extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @Autowired
    private BankDepositService bankDepositService;

    @PreAuthorize("isAuthenticated()")
    public BankDepositTransaction bankForDeposit(Long amount, String currencyCode, String cryptoType) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            new UnauthorizedException(msg, "userId");
        }
        
        // get fiat price, and calculate USD equivalent balance
        double usdAmount = 0.0;
        if(currencyCode.equals("USD")) {
            usdAmount = amount;
        } else {
            double currencyPrice = thirdAPIUtils.getCurrencyRate(currencyCode);
            if(currencyPrice == 0.0) {
                usdAmount = (double) amount;
            }
            usdAmount = (double) amount / currencyPrice;
        }

        // get crypto price, and calculate crypto amount
        double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        double cryptoAmount = usdAmount / cryptoPrice; // total usd
        double fee = getTierFee(userId, cryptoAmount);
        double depoisted = cryptoAmount - fee;

        // generate UID for new bank transfer
        String uid = "";
        BankDepositTransaction uidChecker = null;
        do {
            uid = getBankUID();
            uidChecker = bankDepositService.selectByUid(uid);
        } while (uidChecker != null);

        // get bankDetailsId from currency
        int bankDetailsId = 1; // test

        // create new deposit & 
        var m = new BankDepositTransaction(userId, amount, uid, bankDetailsId, cryptoType, cryptoPrice, currencyCode, usdAmount, fee, depoisted);
        return bankDepositService.insert(m);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int confirmBankDeposit(int id) {
        return bankDepositService.update(id, 1);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<BankDepositTransaction> getAllBankDepositTxns(String orderBy) {
        return (List<BankDepositTransaction>) bankDepositService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<BankDepositTransaction> getBankDepositTxnsByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<BankDepositTransaction>) bankDepositService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public BankDepositTransaction getBankDepositTxnById(int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var m = bankDepositService.selectById(id);
        if(m.getUserId() != userId) return null;
        return (BankDepositTransaction) m;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BankDepositTransaction getBankDepositTxnByIdByAdmin(int id) {
        return (BankDepositTransaction) bankDepositService.selectById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankDepositTransaction> getUnconfirmedBankDepositTxns() {
        return bankDepositService.selectUnconfirmedByAdmin();
    }

    @PreAuthorize("isAuthenticated()")
    public List<BankDepositTransaction> getUnconfirmedBankDepositTxnsByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return bankDepositService.selectUnconfirmedByUser(userId);
    }

}
