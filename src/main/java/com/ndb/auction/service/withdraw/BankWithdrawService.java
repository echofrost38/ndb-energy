package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
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

    public List<BankWithdrawRequest> getRequestsByUser(int userId) {
        return bankWithdrawDao.selectByUser(userId);
    }

    public BankWithdrawRequest getRequestById(int id) {
        return bankWithdrawDao.selectById(id);
    }

    public int approveRequest(int id) {
        int result = bankWithdrawDao.approveRequest(id);
        if(result == 1) {
            // success
            var request = bankWithdrawDao.selectById(id);
            var tokenId = tokenAssetService.getTokenIdBySymbol(request.getSourceToken());
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
            var request = bankWithdrawDao.selectById(id);
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

}
