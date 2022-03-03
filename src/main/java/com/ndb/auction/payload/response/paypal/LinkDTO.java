package com.ndb.auction.payload.response.paypal;

import lombok.Data;

@Data
public class LinkDTO {
    private String href;
    private String rel;
    private String method;
}

