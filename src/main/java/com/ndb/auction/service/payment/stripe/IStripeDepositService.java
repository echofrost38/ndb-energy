package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.models.transactions.StripeDepositTransaction;
import com.ndb.auction.payload.response.PayResponse;

public interface IStripeDepositService {
    public PayResponse createNewTransaction(StripeDepositTransaction _m);
    public List<? extends StripeDepositTransaction> selectByIntentId(String intentId);
}
