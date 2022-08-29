package com.ndb.auction.resolver.payment.deposit;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.exceptions.UserSuspendedException;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.TaskSettingService;
import com.ndb.auction.service.TierService;
import com.ndb.auction.service.payment.bank.BankDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DepositBank extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @Autowired
    private TierService tierService;

    @Autowired
    private TaskSettingService taskSettingService;

    @Autowired
    private InternalBalanceService balanceService;

    @Autowired
    private BankDepositService bankDepositService;

    @PreAuthorize("isAuthenticated()")
    public String bankForDeposit() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        if (userService.isUserSuspended(userId)) {
            String msg = messageSource.getMessage("user_suspended", null, Locale.ENGLISH);
            throw new UserSuspendedException(msg);
        }

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if (!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

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
        var m = new BankDepositTransaction(userId, 0, uid, bankDetailsId, "USDT", 1, "", 0, 0, 0);
        bankDepositService.insert(m);

        return uid;
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public BankDepositTransaction confirmBankDeposit(int id, String currencyCode, double amount, String cryptoType, String code) {

        if(!totpService.checkWithdrawConfirmCode(code)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new BalanceException(msg, "2FA");
        }

        log.info("BANK DEPOSIT CODE CHECKED");

        // ignore show status
        var m = (BankDepositTransaction)bankDepositService.selectById(id, 1);
        if(m == null) {
            throw new UserNotFoundException("There is no such withdrawal request.", "id");
        }

        if(m.getStatus()) {
            throw new UserNotFoundException("This request is already confirmed.", "id");
        }

        var userId = m.getUserId();
        
        // get fiat price, and calculate USD equivalent balance
        double usdAmount = 0.0;
        if(currencyCode.equals("USD")) {
            usdAmount = amount;
        } else {
            double currencyPrice = thirdAPIUtils.getCurrencyRate(currencyCode);
            if(currencyPrice == 0.0) {
                usdAmount = (double) amount;
            } else {
                usdAmount = (double) amount / currencyPrice;
            }
        }

        // get crypto price, and calculate crypto amount
        double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        double cryptoAmount = usdAmount / cryptoPrice; // total usd
        double fee = getTierFee(userId, cryptoAmount);
        double deposited = cryptoAmount - fee;

        log.info("BANK DEPOSIT PRICE FETCHED");

        // update user balance and tier
        internalBalanceService.addFreeBalance(userId, cryptoType, deposited);
        List<BalancePayload> balances = balanceService.getInternalBalances(userId);

        double totalBalance = 0.0;
        for (BalancePayload balance : balances) {
            // get price and total balance
            double _price = thirdAPIUtils.getCryptoPriceBySymbol(balance.getTokenSymbol());
            double _balance = _price * (balance.getFree() + balance.getHold());
            totalBalance += _balance;
        }

        log.info("ADDED BALANCE");

        // update user tier points
        User user = userService.getUserById(userId);
        List<Tier> tierList = tierService.getUserTiers();
        TaskSetting taskSetting = taskSettingService.getTaskSetting();
        TierTask tierTask = tierTaskService.getTierTask(userId);

        if(tierTask == null) {
            tierTask = new TierTask(userId);
            tierTaskService.updateTierTask(tierTask);
        }

        if(tierTask.getWallet() < totalBalance) {

            // get point
            double gainedPoint = 0.0;
            for (WalletTask task : taskSetting.getWallet()) {
                if(tierTask.getWallet() > task.getAmount()) continue;
                if(totalBalance > task.getAmount()) {
                    // add point
                    gainedPoint += task.getPoint();
                } else {
                    break;
                }
            }

            double newPoint = user.getTierPoint() + gainedPoint;
            int tierLevel = 0;
            // check change in level
            for (Tier tier : tierList) {
                if(tier.getPoint() <= newPoint) {
                    tierLevel = tier.getLevel();
                }
            }
            userService.updateTier(user.getId(), tierLevel, newPoint);
            tierTask.setWallet(totalBalance);
            tierTaskService.updateTierTask(tierTask);
        }

        log.info("TIER LEVEL CHECKED");

        notificationService.sendNotification(
            userId,
            Notification.DEPOSIT_SUCCESS, 
            "PAYMENT CONFIRMED", 
            String.format("Your deposit of %f %s was successful.", deposited, cryptoType));

        log.info("SENT NOTIFY");
        
        bankDepositService.update(id, currencyCode, amount, usdAmount, deposited, fee, cryptoType, cryptoPrice);

        log.info("UPDATED DEPOSIT REQUEST");
        // ignore for admin
        return (BankDepositTransaction) bankDepositService.selectById(id, 1);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<BankDepositTransaction> getAllBankDepositTxns(String orderBy) {
        return (List<BankDepositTransaction>) bankDepositService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<BankDepositTransaction> getBankDepositTxnsByUser(String orderBy, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<BankDepositTransaction>) bankDepositService.selectByUser(userId, orderBy, showStatus);
    }

    @PreAuthorize("isAuthenticated()")
    public BankDepositTransaction getBankDepositTxnById(int id, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var m = bankDepositService.selectById(id, showStatus);
        if(m.getUserId() != userId) return null;
        return (BankDepositTransaction) m;
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public BankDepositTransaction getBankDepositTxnByIdByAdmin(int id) {
        return (BankDepositTransaction) bankDepositService.selectById(id, 1);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<BankDepositTransaction> getUnconfirmedBankDepositTxns() {
        return bankDepositService.selectUnconfirmedByAdmin();
    }

    @PreAuthorize("isAuthenticated()")
    public List<BankDepositTransaction> getUnconfirmedBankDepositTxnsByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return bankDepositService.selectUnconfirmedByUser(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public int changeBankDepositShowStatus(int id, int showStatus) {
        return bankDepositService.changeShowStatus(id, showStatus);
    }

}
