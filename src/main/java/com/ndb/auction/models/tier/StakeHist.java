package com.ndb.auction.models.tier;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class StakeHist {

    private Long expiredTime;
    private Long amount;

}
