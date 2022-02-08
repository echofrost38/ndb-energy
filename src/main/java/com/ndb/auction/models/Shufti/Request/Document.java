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
public class Document {

    public Document(String proof) {
        this.proof = proof;
        this.supported_types = new ArrayList<>();
        this.supported_types.add("passport");
    }

    private String proof;
    private String additional_proof;
    private List<String> supported_types;
    private Name name;
}
