package com.ndb.auction.payload.response.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SenderBatchHeader {
    private String sender_batch_id;
    private String email_subject;
    private String email_message;
}
