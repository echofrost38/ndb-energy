package com.ndb.auction.models.transactions.stripe;

import com.ndb.auction.models.transactions.FiatDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeDepositTransaction extends FiatDepositTransaction {
    
    public static final Integer INITIATED = 0;
	public static final Integer AUTHORIZED = 1;
	public static final Integer CAPTURED = 2;
	public static final Integer CANCELED = 3;
    
    protected String paymentMethodId;
    protected String paymentIntentId;

    public StripeDepositTransaction(
        int userId,
        Long amount,
        String paymentIntentId,
        String paymentMethodId
    ) {
        this.userId = userId;
        this.amount = amount;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
    }
}
