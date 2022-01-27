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
public class Document {
    private String proof;
    private String additional_proof;
    private List<String> supported_types;
    private Name name;
}
