package com.ndb.auction.models.nyyupay;

import com.ndb.auction.models.BaseModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyyuPayPendingRequest extends BaseModel {
    private String address;
    private String callback;
    private String network;
}
