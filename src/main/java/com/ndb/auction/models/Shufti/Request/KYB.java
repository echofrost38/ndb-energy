package com.ndb.auction.models.Shufti.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KYB {
    private String company_registration_number;
    private String company_jurisdiction_code;
    private String company_name;
}
