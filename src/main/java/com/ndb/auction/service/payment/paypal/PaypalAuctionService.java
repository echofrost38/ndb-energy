package com.ndb.auction.service.payment.paypal;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.paypal.PaypalTransactionDao;
import com.ndb.auction.models.transactions.paypal.PaypalTransaction;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaypalAuctionService extends PaypalBaseService {

    private final PaypalTransactionDao paypalTransactionDao;

    // Create new PayPal order
    public PaypalTransaction insert(PaypalTransaction m) {
        return paypalTransactionDao.insert(m);
    }

    public List<PaypalTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return paypalTransactionDao.selectPage(status, showStatus, offset, limit, "AUCTION", orderBy);
    }

    public List<PaypalTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        return paypalTransactionDao.selectByUser(userId, showStatus, orderBy);
    }

    public PaypalTransaction selectById(int id) {
        return paypalTransactionDao.selectById(id);
    }

    public int updateOrderStatus(int id, String status) {
        return paypalTransactionDao.updateOrderStatus(id, status);
    }
    
}
