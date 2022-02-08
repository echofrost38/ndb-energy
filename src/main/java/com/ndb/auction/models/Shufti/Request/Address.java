package com.ndb.auction.models.Shufti.Request;

import java.util.ArrayList;
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

    public Address(String proof, String fullAddress) {
        this.proof = proof;
        this.supported_types = new ArrayList<>();
        this.supported_types.add("id_card");
        this.full_address = fullAddress;
    }

    private String proof;
    private List<String> supported_types;
    private Name name;
    private String full_address;
}
