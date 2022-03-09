package com.ndb.auction.payload.response.paypal.BatchHeader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class BatchHeader {
    private String payout_batch_id;
    private String batch_status;
    private SenderBatchHeader sender_batch_header;
    private Amount amount;
}
