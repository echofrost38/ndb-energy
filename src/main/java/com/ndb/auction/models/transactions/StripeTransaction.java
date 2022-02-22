package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeTransaction extends FiatDepositTransaction {
    private String paymentMethodId;
    private String paymentIntentId;
}
