package com.ndb.auction.dao.oracle.transactions.stripe;

import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;

public interface IStripeDao {
    StripeDepositTransaction selectByStripeIntentId(String intentId);
}
