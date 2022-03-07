package com.ndb.auction.payload.response.paypal.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ndb.auction.payload.response.paypal.Amount;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseUnit {
    private Amount amount;
}
