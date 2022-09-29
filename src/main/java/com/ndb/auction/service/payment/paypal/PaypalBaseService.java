package com.ndb.auction.service.payment.paypal;

import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaypalBaseService extends BaseService {
    
    @Value("${website.url}")
	protected String WEBSITE_URL;

    private final double PAYPAL_FEE = 3.49;
    
    // get order by OrderID
    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        return paypalAuctionDao.selectByPaypalOrderId(orderId);
    }

    public double getPayPalTotalOrder(int userId, double amount) {
		User user = userDao.selectById(userId);
		Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
        var white = whitelistDao.selectByUserId(userId);
		if(white != null) tierFeeRate = 0.0;
		return 100 * (amount + 0.49) / (100 - PAYPAL_FEE - tierFeeRate);
	}
}
