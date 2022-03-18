package com.ndb.auction.service.payment.paypal;

import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Value;

public class PaypalBaseService extends BaseService {
    
    @Value("${website.url}")
	protected String WEBSITE_URL;

    private final double PAYPAL_FEE = 5;
    
    // get order by OrderID
    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        return paypalAuctionDao.selectByPaypalOrderId(orderId);
    }

    protected double getPayPalTotalOrder(int userId, double amount) {
		User user = userDao.selectById(userId);
		Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
        var white = whitelistDao.selectByUserId(userId);
		if(white != null) tierFeeRate = 0.0;
		return 100 * (amount + 0.30) / (100 - PAYPAL_FEE - tierFeeRate);
	}
}
