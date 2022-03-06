package com.ndb.auction.payload.request.paypal;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayoutsDTO {

    public PayoutsDTO(SenderBatchHeader header, Item item) {
        this.sender_batch_header = header;
        this.items = new ArrayList<>();
        items.add(item);
    }

    private SenderBatchHeader sender_batch_header;
    private List<Item> items;
}
