package com.ndb.auction.models.Shufti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShuftiReference {
    
    public ShuftiReference(int userId, String reference) {
        this.userId = userId;
        this.reference = reference;
        this.verificationType = "KYC";

        this.docStatus = false;
        this.addrStatus = false;
        this.conStatus = false;
        this.selfieStatus = false;

        this.pending = false;
    }
    
    private int userId;
    private String reference;
    private String verificationType;

    // document
    private Boolean docStatus; // success or failed

    // address
    private Boolean addrStatus;

    // consent
    private Boolean conStatus;

    // selfie
    private Boolean selfieStatus;

    private Boolean pending;
}
