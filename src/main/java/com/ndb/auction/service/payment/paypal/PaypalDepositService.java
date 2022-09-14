package com.ndb.auction.service.payment.paypal;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.dao.oracle.transactions.paypal.PaypalTransactionDao;
import com.ndb.auction.models.transactions.paypal.PaypalTransaction;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaypalDepositService extends PaypalBaseService {

    private final PaypalTransactionDao paypalTransactionDao;

    public PaypalTransaction insert(PaypalTransaction m) {
        return paypalTransactionDao.insert(m);
    }

    public List<PaypalTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String txnType, String orderBy) {
        return paypalTransactionDao.selectPage(status, showStatus, offset, limit, txnType, orderBy);
    }

    public List<PaypalTransaction> selectByUser(int userId, String orderBy, int status) {
        return paypalTransactionDao.selectByUser(userId, status, orderBy);
    }

    public PaypalTransaction selectById(int id) {
        return paypalTransactionDao.selectById(id);
    }

    public int updateOrderStatus(int id, String orderStatus) {
        return paypalTransactionDao.updateOrderStatus(id, orderStatus);
    }

    public PaypalTransaction selectByPaypalOrderId(String orderId) {
        return paypalTransactionDao.selectByOrderId(orderId);
    }
    
    public int changeShowStatus(int id, int status) {
        return paypalTransactionDao.changeShowStatus(id, status);
    }
}
