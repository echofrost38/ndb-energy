package com.ndb.auction.payload.response.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Amount {
    private String value;
    private String currency;
}
