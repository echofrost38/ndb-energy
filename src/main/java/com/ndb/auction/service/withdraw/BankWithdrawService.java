package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankWithdrawService extends BaseService{
    
    @Autowired
    private BankWithdrawDao bankWithdrawDao;

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
        return bankWithdrawDao.approveRequest(id);
    }

    public int denyRequest(int id, String reason) {
        return bankWithdrawDao.denyRequest(id, reason);
    }

}
