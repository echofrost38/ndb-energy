package com.ndb.auction.payload.response.paypal.BatchHeader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SenderBatchHeader {
    private String sender_batch_id;
}
