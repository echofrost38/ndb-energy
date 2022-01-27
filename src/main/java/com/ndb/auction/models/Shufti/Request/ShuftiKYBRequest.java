package com.ndb.auction.models.Shufti.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShuftiKYBRequest {
    private KYB kyb;
    private AMLForBusiness aml_for_businesses;
}
