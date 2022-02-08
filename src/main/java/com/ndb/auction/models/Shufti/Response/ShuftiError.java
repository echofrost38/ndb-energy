package com.ndb.auction.models.Shufti.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShuftiError {
    private String service;
    private String key;
    private String message;
}
