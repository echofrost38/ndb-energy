package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class DirectSale extends BaseModel {

    // CONSTANTS
    public static final int STRIPE = 0;
    public static final int CRYPTO = 1;
    public static final int WALLET = 2;
    public static final int INTERNAL = 0;
    public static final int EXTERNAL = 1;

    private int userId;
    private String txnId;

    // stripe and coinbase, ndb wallet
    private int payType;
    private String ndbPrice;
    private String ndbAmount;
    private int whereTo;

    private String extAddr;

    private boolean isConfirmed;

    private long createdAt;
    private long confirmedAt;

    // for stripe
    private String paymentIntentId;

    // for coinbase
    private String code;
    private String cryptoType;
    private String cryptoAmount;

    public DirectSale(
        int userId,
        String txnId,
        String ndbPrice,
        String ndbAmount,
        int whereTo,
        String extAddr
    ) {
        this.userId = userId;
        this.txnId = txnId;
        this.ndbPrice = ndbPrice;
        this.ndbAmount = ndbAmount;
        this.whereTo = whereTo;
        this.extAddr = extAddr;
        this.isConfirmed = false;
        this.createdAt = System.currentTimeMillis();
    }

}
