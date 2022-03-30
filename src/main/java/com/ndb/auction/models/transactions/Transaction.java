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
    protected Long amount;
    protected Long createdAt;
    protected Long confirmedAt;
    protected Boolean status;
}
