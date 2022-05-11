package com.ndb.auction.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class WithdrawRequest {
    private String withdrawType;
    private String avatarName;
    private String email;
    private String fullName;
    private String address;
    private String country;
    private double balance;
    private String currency;
    private double requestAmount;
    private String requestCurrency;
    private String typeMessage;
    private String destination;
    private String bankMetadata;
}
