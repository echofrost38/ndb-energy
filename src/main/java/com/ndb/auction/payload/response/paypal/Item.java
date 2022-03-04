package com.ndb.auction.payload.response.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item {
    private String recipient_type;
    private Amount amount;
    private String note;
    private String sender_item_id;
    private String receiver;
}
