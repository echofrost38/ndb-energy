package com.ndb.auction.service.withdraw;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankWithdrawService extends BaseService{
    
    @Autowired
    private BankWithdrawDao bankWithdrawDao;

    @Autowired
    private CryptoBalanceDao balanceDao;

    // create new withdraw 
    public int createNewRequest(BankWithdrawRequest m) {
        return bankWithdrawDao.insert(m);
    }

    public List<BankWithdrawRequest> getAllPendingRequests() {
        return bankWithdrawDao.selectPending();
    }

    public List<BankWithdrawRequest> getAllApproved() {
        return bankWithdrawDao.selectApproved();
    }

    public List<BankWithdrawRequest> getAllDenied() {
        return bankWithdrawDao.selectDenied();   
    }

    public List<BankWithdrawRequest> getAllRequests() {
        return bankWithdrawDao.selectAll();
    }

    public List<BankWithdrawRequest> getRequestsByUser(int userId, int status) {
        return bankWithdrawDao.selectByUser(userId, status);
    }

    public BankWithdrawRequest getRequestById(int id, int status) {
        return bankWithdrawDao.selectById(id, status);
    }

    public int approveRequest(int id) {
        int result = bankWithdrawDao.approveRequest(id);
        if(result == 1) {
            // success
            // ignore show status
            var request = bankWithdrawDao.selectById(id, 1);
            var tokenId = tokenAssetService.getTokenIdBySymbol(request.getSourceToken());

            // get free balance
            var balance = balanceDao.selectById(request.getUserId(), tokenId);
            if(balance.getFree() < request.getTokenAmount()) {
                String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
                throw new BalanceException(msg, "amount");
            }

            balanceDao.deductFreeBalance(request.getUserId(), tokenId, request.getTokenAmount());

            notificationService.sendNotification(
                    request.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    "Your withdarwal request has been approved");
            return result;
        } else {
            return result;
        }
    }

    public int denyRequest(int id, String reason) {
        int result = bankWithdrawDao.denyRequest(id, reason);
        if(result == 1) {
            // success
            var request = bankWithdrawDao.selectById(id, 1);
            notificationService.sendNotification(
                    request.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    String.format("Your withdarwal request has been denied. %s", reason));
            return result;
        } else {
            return result;
        }
    }

    public int changeShowStatus(int id, int showStatus) {
        return bankWithdrawDao.changeShowStatus(id, showStatus);
    }

}
