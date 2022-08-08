package com.ndb.auction.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NyyuWalletResponse {
    private int status;
    private String address;
    private String error;
}
