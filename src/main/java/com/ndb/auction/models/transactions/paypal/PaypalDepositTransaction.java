package com.ndb.auction.models.transactions.paypal;

import com.ndb.auction.models.transactions.FiatDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaypalDepositTransaction extends FiatDepositTransaction {
    
    protected String paypalOrderId;
    protected String paypalOrderStatus;

    public PaypalDepositTransaction(
        int userId, 
        Long amount, 
        String paypalOrderId,
        String paypalOrderStatus
    ) {
        this.userId = userId;
        this.amount = amount;
        this.paypalOrderId = paypalOrderId;
        this.paypalOrderStatus = paypalOrderStatus;
    }

}
