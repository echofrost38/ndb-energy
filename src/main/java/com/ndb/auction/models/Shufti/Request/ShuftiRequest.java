package com.ndb.auction.models.Shufti.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShuftiRequest {
    private String reference;
    private String country;
    private String email;
    private String callback_url;
    private Face face;
    private Document document;
    private Address address;
    private BackgroundChecks background_checks;
    private Consent consent;
}
