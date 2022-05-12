package com.ndb.auction.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BankMeta {
    private String name;
    private String address;
    private String swift;
    private String iban;
}
