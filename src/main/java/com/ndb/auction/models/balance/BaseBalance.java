package com.ndb.auction.models.balance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseBalance {
    protected int userId;
    protected Double free;
    protected Double hold; 
}
