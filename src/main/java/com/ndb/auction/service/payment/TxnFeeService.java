package com.ndb.auction.service.payment;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.models.transactions.TxnFee;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class TxnFeeService extends BaseService {
    
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

}
