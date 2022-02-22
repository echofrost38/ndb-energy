package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    protected int id;
    protected int userId;
    protected Double amount;
    protected Long createdAt;
    protected Boolean status;
}
