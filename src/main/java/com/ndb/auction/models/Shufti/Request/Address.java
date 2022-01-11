package com.ndb.auction.models.Shufti.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String proof;
    private List<String> supported_types;
    private Name name;
    private String full_address;
}
