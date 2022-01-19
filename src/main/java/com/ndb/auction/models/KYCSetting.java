package com.ndb.auction.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KYCSetting {
    
    private String kind;
    private Double withdraw;
    private Double deposit;
    private Double bid;
    private Double direct;

}
