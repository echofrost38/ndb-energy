package com.ndb.auction.models.tier;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class WalletTask {

    private long amount;
    private long point;

}
