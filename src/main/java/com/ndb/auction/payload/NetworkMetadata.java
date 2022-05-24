package com.ndb.auction.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NetworkMetadata {
    private String network;
    private String jsonRpc;
    private String walletAddr;
    private String walletKey;
}
