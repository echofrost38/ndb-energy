package com.ndb.auction.models.Shufti.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResult {
    // kyc
    private int kyc;
    // background_checks
    private int background_checks;
    // kyb
    private int kyb;
    // aml_for_businesses
    private int aml_for_businesses;
}
