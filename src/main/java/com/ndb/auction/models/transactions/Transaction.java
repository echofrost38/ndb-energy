package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    private int id;
    private int userId;
    private Double amount;
    private Long createdAt;
    private Boolean status;
}
