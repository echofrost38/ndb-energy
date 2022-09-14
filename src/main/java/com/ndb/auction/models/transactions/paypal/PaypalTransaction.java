package com.ndb.auction.models.transactions.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaypalTransaction {
    private int id;
    private int userId;

    private String txnType;
    private int txnId;

    private String fiatType;
    private double fiatAmount;
    private double usdAmount;
    private double fee;

    private String paypalOrderId;
    private String paypalOrderStatus;

    private String cryptoType;
    private double cryptoAmount;

    private int status;
    private boolean shown;

    private long createdAt;
    private long updatedAt;
}
