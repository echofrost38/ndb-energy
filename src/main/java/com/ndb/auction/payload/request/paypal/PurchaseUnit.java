package com.ndb.auction.payload.request.paypal;

import lombok.Data;

@Data
public class PurchaseUnit {
    private MoneyDTO amount;

    public PurchaseUnit(String amount, String currencyCode) {
        this.amount = new MoneyDTO(amount, currencyCode);
    }
}
