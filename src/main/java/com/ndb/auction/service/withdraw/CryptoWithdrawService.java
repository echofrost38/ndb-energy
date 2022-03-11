package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptoWithdrawService extends BaseService implements IWithdrawService {

    @Autowired
    private CryptoWithdrawDao cryptoWithdrawDao;

    @Override
    public BaseWithdraw createNewWithdrawRequest(BaseWithdraw baseWithdraw) {
        var m = (CryptoWithdraw)baseWithdraw;
        return cryptoWithdrawDao.insert(m);
    }

    @Override
    public int confirmWithdrawRequest(int requestId, int status, String reason) throws Exception {
        return cryptoWithdrawDao.confirmWithdrawRequest(requestId, status, reason);
    }

    @Override
    public List<? extends BaseWithdraw> getWithdrawRequestByUser(int userId) {
        return cryptoWithdrawDao.selectByUser(userId);
    }

    @Override
    public List<? extends BaseWithdraw> getWithdrawRequestByStatus(int userId, int status) {
        return cryptoWithdrawDao.selectByStatus(userId, status);
    }

    @Override
    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests() {
        return cryptoWithdrawDao.selectPendings();
    }

    @Override
    public BaseWithdraw getWithdrawRequestById(int id) {
        return cryptoWithdrawDao.selectById(id);
    }
    
}
