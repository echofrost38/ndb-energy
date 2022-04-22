package com.ndb.auction.models.transactions.paypal;

import com.ndb.auction.models.transactions.FiatDepositTransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaypalDepositTransaction extends FiatDepositTransaction {
    
    protected String paypalOrderId;
    protected String paypalOrderStatus;

    private String cryptoType;
    private Double cryptoPrice;
    protected Double fee;
    private Double deposited; // crypto amount!

    public PaypalDepositTransaction(
        int userId, 
        Double amount, // usd amount
        double fiatAmount,
        String fiatType,
        String cryptoType,
        Double cryptoPrice,
        String paypalOrderId,
        String paypalOrderStatus, 
        Double fee, 
        Double deposited
    ) {
        this.userId = userId;
        this.amount = amount; // usd
        this.fiatAmount = fiatAmount;
        this.fiatType = fiatType;
        this.cryptoType = cryptoType;
        this.cryptoPrice = cryptoPrice;
        this.paypalOrderId = paypalOrderId;
        this.paypalOrderStatus = paypalOrderStatus;
        this.fee = fee;
        this.deposited = deposited;
    }

}
