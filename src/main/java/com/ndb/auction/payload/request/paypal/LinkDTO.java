package com.ndb.auction.payload.request.paypal;

import lombok.Data;

@Data
public class LinkDTO {
    private String href;
    private String rel;
    private String method;
}

