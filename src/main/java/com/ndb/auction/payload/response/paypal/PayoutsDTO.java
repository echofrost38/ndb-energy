package com.ndb.auction.payload.response.paypal;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayoutsDTO {
    private SenderBatchHeader sender_batch_header;
    private List<Item> items;
}
