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

    public ShuftiRequest(
        String reference, 
        String country, 
        String doc, 
        String addr, 
        String fullAddr, 
        String consent, 
        String face,
        Names names
    ) {
        this.reference = reference;
        this.country = country;
        this.document = new Document(doc);
        this.address = new Address(addr, fullAddr);
        this.consent = new Consent(consent);
        this.face = new Face(face);
        this.background_checks = new BackgroundChecks(names);
        // this.callback_url = "https://9ce6-80-237-47-16.ngrok.io/shufti";
    }

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
