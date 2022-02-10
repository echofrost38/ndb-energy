package com.ndb.auction.payload.response;

import com.ndb.auction.models.Shufti.ShuftiReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShuftiRefPayload extends ShuftiReference{

    public ShuftiRefPayload(ShuftiReference ref) {
        super(
            ref.getUserId(), 
            ref.getReference(), 
            ref.getVerificationType(), 
            ref.getDocStatus(), 
            ref.getAddrStatus(), 
            ref.getConStatus(), 
            ref.getSelfieStatus(),
            ref.getPending()
        );
    }
    // base64 for doc, addr, consent and selfie
    private String document;
    private String addr;
    private String consent;
    private String selfie;
}
