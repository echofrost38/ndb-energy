package com.ndb.auction.payload.response.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BatchHeader {
    private String payout_batch_id;
    private String batch_status;
}
