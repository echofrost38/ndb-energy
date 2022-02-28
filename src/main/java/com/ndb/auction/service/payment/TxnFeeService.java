package com.ndb.auction.service.payment;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.dao.oracle.transactions.coinpayment.TxnFeeDao;
import com.ndb.auction.models.transactions.TxnFee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TxnFeeService {
    
    @Autowired
    private TxnFeeDao txnFeeDao;

    // cache
    private List<TxnFee> txnFeeList;

    private synchronized void fillList() {
        if(txnFeeList == null) {
            txnFeeList = new ArrayList<TxnFee>();
        } else {
            txnFeeList.clear();
        }
        txnFeeList = txnFeeDao.selectAll();
    }

    public TxnFeeService() {
        this.txnFeeList = null;
    }

    // insert new fee
    public List<TxnFee> insert(TxnFee m) {
        txnFeeDao.insert(m);
        fillList();
        return txnFeeList;
    }

    public List<TxnFee> selectAll() {
        if(txnFeeList == null) {
            fillList();
        }
        return txnFeeList;
    }

    public List<TxnFee> update(TxnFee m) {
        txnFeeDao.update(m);
        fillList();
        return txnFeeList;
    }

    public List<TxnFee> delete(int id) {
        txnFeeDao.deleteById(id);
        fillList();
        return txnFeeList;
    }

    public double getFee(int tierLevel) {
        if(txnFeeList == null) fillList();
        for (TxnFee txnFee : txnFeeList) {
            if(txnFee.getTierLevel() == tierLevel) {
                return txnFee.getFee();
            }
        }
        return 0.0;
    }

}
