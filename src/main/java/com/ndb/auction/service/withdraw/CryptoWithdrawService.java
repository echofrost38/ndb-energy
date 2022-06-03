package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptoWithdrawService extends BaseService {

    @Autowired
    private CryptoWithdrawDao cryptoWithdrawDao;

    public BaseWithdraw createNewWithdrawRequest(BaseWithdraw baseWithdraw) {
        var m = (CryptoWithdraw)baseWithdraw;
        return cryptoWithdrawDao.insert(m);
    }

    public int confirmWithdrawRequest(int requestId, int status, String reason) throws Exception {
        return cryptoWithdrawDao.confirmWithdrawRequest(requestId, status, reason);
    }

    public int updateCryptoWithdrawTxHash(int withdrawId, String hash) {
        return cryptoWithdrawDao.updateCryptoWithdarwTxHash(withdrawId, hash);
    }

    public List<? extends BaseWithdraw> getWithdrawRequestByUser(int userId, int status) {
        return cryptoWithdrawDao.selectByUser(userId, status);
    }

    public List<? extends BaseWithdraw> getWithdrawRequestByStatus(int userId, int approvedStatus) {
        return cryptoWithdrawDao.selectByStatus(userId, approvedStatus);
    }

    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests() {
        return cryptoWithdrawDao.selectPendings();
    }

    public List<? extends BaseWithdraw> getAllWithdrawRequests() {
        return cryptoWithdrawDao.selectAll();
    }

    public BaseWithdraw getWithdrawRequestById(int id, int showStatus) {
        return cryptoWithdrawDao.selectById(id, showStatus);
    }

    public int changeShowStatus(int id, int showStatus) {
        return cryptoWithdrawDao.changeShowStatus(id, showStatus);
    }
    
}
