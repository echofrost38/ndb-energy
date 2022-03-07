package com.ndb.auction.service.payment.paypal;

import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.service.BaseService;

public class PaypalBaseService extends BaseService {
    // get order by OrderID
    public PaypalDepositTransaction selectByOrderId(String orderId) {
        return paypalAuctionDao.selectByOrderId(orderId);
    }
}
