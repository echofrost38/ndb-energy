package com.ndb.auction.models.wallet;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class NyyuWallet {
    protected int id;
    protected int userId;
    protected String publicKey;
    protected String privateKey;
    protected String network;
    protected Boolean nyyuPayRegistered;
}