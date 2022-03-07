package com.ndb.auction.dao.oracle.transactions.paypal;

import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;

public interface IPaypalDao {
    public PaypalDepositTransaction selectByOrderId(String orderId);
}
