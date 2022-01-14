package com.ndb.auction.models.Shufti.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShuftiBusResponse {
    private String reference;
    private String event;
    private String email;
    private String country;
    private VerificationData verification_data;
    private VerificationResult verification_result;
}
