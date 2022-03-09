package com.ndb.auction.payload.response.paypal.BatchHeader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Amount {
    private String currency;
    private String value;
}
