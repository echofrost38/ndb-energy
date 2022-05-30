package com.ndb.auction.service.payment.paypal;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaypalDepositService extends PaypalBaseService {

    @Autowired
    private PaypalDepositDao paypalDepositDao;

    public PaypalDepositTransaction insert(PaypalDepositTransaction m) {
        return (PaypalDepositTransaction) paypalDepositDao.insert(m);
    }

    public List<? extends Transaction> selectAll(String orderBy) {
        return paypalDepositDao.selectAll(orderBy);
    }

    public List<? extends Transaction> selectByUser(int userId, String orderBy, int status) {
        return paypalDepositDao.selectByUser(userId, orderBy, status);
    }

    public Transaction selectById(int id, int status) {
        return paypalDepositDao.selectById(id, status);
    }

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
    
    public int changeShowStatus(int id, int status) {
        return paypalDepositDao.changeShowStatus(id, status);
    }
}
