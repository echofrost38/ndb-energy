package com.ndb.auction.models.Shufti.Request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Consent {

    public Consent(String proof) {
        this.proof = proof;
        this.text = "I & NDB";
        this.supported_types = new ArrayList<>();
        supported_types.add("handwritten");
    }

    private String proof;
    private String text;
    private List<String> supported_types;
}
