package com.ndb.auction.payload.request.paypal;

import com.paypal.api.payments.Amount;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item {

    public Item(Amount amount, String sender_item_id, String receiver) {
        this.amount = amount;
        this.sender_item_id = sender_item_id;
        this.receiver = receiver;
        this.recipient_wallet = "PAYPAL";
    }

    private String recipient_wallet;
    private Amount amount;
    private String note;
    private String sender_item_id;
    private String receiver;
}
