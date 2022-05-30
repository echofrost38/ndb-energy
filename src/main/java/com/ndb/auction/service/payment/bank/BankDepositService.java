package com.ndb.auction.service.payment.bank;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.bank.BankDepositDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankDepositService {

    @Autowired
    private BankDepositDao bankDepositDao;

    public BankDepositTransaction insert(BankDepositTransaction m) {
        return (BankDepositTransaction) bankDepositDao.insert(m);
    }

    public List<? extends Transaction> selectAll(String orderBy) {
        return bankDepositDao.selectAll(orderBy);
    }

    public List<? extends Transaction> selectByUser(int userId, String orderBy, int status) {
        return bankDepositDao.selectByUser(userId, orderBy, status);
    }

    public Transaction selectById(int id, int status) {
        return bankDepositDao.selectById(id, status);
    }

    public int update(int id, int status) {
        return bankDepositDao.update(id, status);
    }

    public int update(int id, String currency, double amount, double usdAmount, double deposited, double fee, String cryptoType, double cryptoPrice) {
        return bankDepositDao.update(id, currency, amount, usdAmount, deposited, fee, cryptoType, cryptoPrice);
    }

    public BankDepositTransaction selectByUid(String uid) {
        return bankDepositDao.selectByUid(uid);
    }

    public List<BankDepositTransaction> selectUnconfirmedByAdmin() {
        return bankDepositDao.selectUnconfirmedByAdmin();
    }

    public List<BankDepositTransaction> selectUnconfirmedByUser(int userId) {
        return bankDepositDao.selectUnconfirmedByUser(userId);
    }
    
    public int changeShowStatus(int id, int isShow) {
        return bankDepositDao.changeShowStatus(id, isShow);
    }
}
