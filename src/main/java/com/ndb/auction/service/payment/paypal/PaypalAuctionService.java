package com.ndb.auction.service.payment.paypal;

import java.util.List;

import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.service.payment.ITransactionService;

import org.springframework.stereotype.Service;

@Service
public class PaypalAuctionService extends PaypalBaseService implements ITransactionService {

    // Create new PayPal order
    public Transaction insert(PaypalAuctionTransaction m) {
        return paypalAuctionDao.insert(m);
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return paypalAuctionDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return paypalAuctionDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return paypalAuctionDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int updateOrderStatus(int id, String status) {
        return paypalAuctionDao.updateOrderStatus(id, status);
    }
    
}
