package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiatTransaction extends Transaction {
    protected String fiatType;
    protected Double fiatAmount;
}
