package com.ndb.auction.service.payment.paypal;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.service.payment.ITransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaypalDepositService extends PaypalBaseService implements ITransactionService {

    @Autowired
    private PaypalDepositDao paypalDepositDao;

    public PaypalDepositTransaction insert(PaypalDepositTransaction m) {
        return (PaypalDepositTransaction) paypalDepositDao.insert(m);
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return paypalDepositDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return paypalDepositDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return paypalDepositDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int updateOrderStatus(int id, String status) {
        return paypalDepositDao.updateOrderStatus(id, status);
    }

    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        return paypalDepositDao.selectByPaypalOrderId(orderId);
    }
    
}
