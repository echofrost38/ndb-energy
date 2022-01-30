package com.ndb.auction.service.payment;

import java.util.List;

import com.ndb.auction.models.Notification;
import com.ndb.auction.models.transaction.WithdrawTransaction;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class WithdrawService extends BaseService {
    
    public int createNewWithdrawTxn(WithdrawTransaction m) {
        return withdrawTxnDao.insert(m);
    }

    public int updateTxn(String from, String to, Double value, String blockNumber, String txnHash) {
        return withdrawTxnDao.update(from, to, value, blockNumber, txnHash);
    }

    public int updateStatus(String txnHash) {
        WithdrawTransaction m = withdrawTxnDao.selectByHash(txnHash);
        notificationService.sendNotification(
            m.getUserId(),
            Notification.WITHDRAW_SUCCESS, 
            "Withdrawal Successful", 
            String.format("You have successfully withdrawan %f NDB", m.getValue()));

        return withdrawTxnDao.udpateStatus(txnHash);
    }

    public WithdrawTransaction selectByHash(String txnHash) {
        return withdrawTxnDao.selectByHash(txnHash);
    }

    public List<WithdrawTransaction> selectByUser(int userId) {
        return withdrawTxnDao.selectyByUser(userId);
    }

}
