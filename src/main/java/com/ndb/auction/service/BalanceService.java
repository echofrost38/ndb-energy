package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.models.InternalBalance;

import org.springframework.stereotype.Service;

@Service
public class BalanceService extends BaseService {
    // create new balance
    public int insertNewBalance(int userId, int tokenId) {
        InternalBalance balance = balanceDao.selectById(userId, tokenId);
        if(balance != null) {
            return 0;
        }
        balance = new InternalBalance(userId, tokenId);
        return balanceDao.insert(balance);
    }

    // Get Balances by user id
    public List<InternalBalance> getBalancesByUserId(int userId, String orderby) {
        return balanceDao.selectByUserId(userId, orderby);
    }

    // update parts
    // add free
    public int addFree(int userId, int tokenId, double amounts) {
        InternalBalance balance = balanceDao.selectById(userId, tokenId);
        if(balance == null) { 
            return -1;
        }
        balance.setFree(balance.getFree() + amounts);
        return balanceDao.update(balance);
    }
    // subtract free amount, withdraw
    public int subFree(int userId, int tokenId, double amounts) {
        InternalBalance balance = balanceDao.selectById(userId, tokenId);
        if(balance == null) {
            return -1;
        }
        double _free = balance.getFree();
        if(_free < amounts) {
            balance.setFree(0.0);
        } else {
            balance.setFree(_free - amounts);
        }
        return balanceDao.update(balance);
    }

    // make hold
    public int makeHold(int userId, int tokenId, double amounts) {
        InternalBalance balance = balanceDao.selectById(userId, tokenId);
        if(balance == null) {
            return -1;
        }
        double free = balance.getFree();
        if(free < amounts) {
            throw new BalanceException("You have no enough free balance.", "free");
        }
        double hold = balance.getHold();
        balance.setFree(free - amounts);
        balance.setHold(hold + amounts);
        return balanceDao.update(balance);
    }

    public int releaseHold(int userId, int tokenId, double amounts) {
        InternalBalance balance = balanceDao.selectById(userId, tokenId);
        if(balance == null) {
            return -1;
        }
        double free = balance.getFree();
        double hold = balance.getHold();
        if(hold < amounts) {
            throw new BalanceException("There is no enough hold balance.", "hold");
        }
        balance.setFree(free + amounts);
        balance.setHold(hold - amounts);
        return balanceDao.update(balance);
    }

}
