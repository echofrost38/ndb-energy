package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class StripeCustomerService extends BaseService {
    
    /// getting saved card per user
    public List<StripeCustomer> getSavedCards(int userId) {
        return stripeCustomerDao.selectByUser(userId);
    }

    public StripeCustomer getSavedCard(int id) {
        return stripeCustomerDao.selectById(id);
    }

    // delete one 
    public int deleteStripeCustomer(int id) {
        return stripeCustomerDao.deleteById(id);
    }

}
