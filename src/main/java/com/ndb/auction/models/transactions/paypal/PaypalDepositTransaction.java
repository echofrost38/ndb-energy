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
    private Double fee;
    private Double deposited; // crypto amount!

    public PaypalDepositTransaction(
        int userId, 
        Long amount, // pay amount
        String cryptoType,
        Double cryptoPrice,
        String paypalOrderId,
        String paypalOrderStatus, 
        Double fee, 
        Double deposited
    ) {
        this.userId = userId;
        this.amount = amount;
        this.fiatAmount = amount;
        this.fiatType = "USD";
        this.cryptoType = cryptoType;
        this.cryptoPrice = cryptoPrice;
        this.paypalOrderId = paypalOrderId;
        this.paypalOrderStatus = paypalOrderStatus;
        this.fee = fee;
        this.deposited = deposited;
    }

}
