package com.ndb.auction.models.Shufti.Request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Consent {
    private String proof;
    private String text;
    private List<String> supported_types;
}
