package com.ndb.auction.payload.request.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SenderBatchHeader {

    public SenderBatchHeader(String sender_batch_id) {
        this.sender_batch_id = sender_batch_id;
        this.recipient_type = "EMAIL";
        this.email_message = "You received a withdraw payment.";
        this.email_subject = "NDB Auction withdraw";
    }

    private String sender_batch_id;
    private String recipient_type;
    private String email_subject;
    private String email_message;
}
