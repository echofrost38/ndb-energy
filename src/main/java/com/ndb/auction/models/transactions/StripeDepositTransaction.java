package com.ndb.auction.models.transactions;

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
}
