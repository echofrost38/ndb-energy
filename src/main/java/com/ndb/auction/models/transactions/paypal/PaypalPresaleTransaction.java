package com.ndb.auction.models.transactions.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaypalPresaleTransaction extends PaypalDepositTransaction {
    private int presaleId;
    private int orderId;

    public PaypalPresaleTransaction(
        int userId,
        int presaleId,
        int orderId,
        double amount,
        double fee,
        String paypalOrderId,
        String paypalOrderStatus
    ) {
        this.userId = userId;
        this.presaleId = presaleId;
        this.orderId = orderId;
        this.amount = amount;
        this.fee = fee;
        this.fiatAmount = amount;
        this.fiatType = "USD";
        this.paypalOrderId = paypalOrderId;
        this.paypalOrderStatus = paypalOrderStatus;
    }
}
