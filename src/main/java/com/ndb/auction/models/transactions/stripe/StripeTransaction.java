package com.ndb.auction.models.transactions.stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeTransaction {

	public static final Integer INITIATED = 0;
	public static final Integer AUTHORIZED = 1;
	public static final Integer CAPTURED = 2;
	public static final Integer CANCELED = 3;

    // identifiers
    private int id;
    private int userId;

    // tranaction type
    private String txnType; // DEPOSIT, PRESALE, BID
    private int txnId; // presale order id or bid id

    // transaction details
    private String intentId;
    private String methodId;

    private String fiatType;
    private double fiatAmount;
    private double usdAmount;
    private double fee; // fee is in USD

    // target fiat will be deposited into crypto assets
    private String cryptoType;
    private double cryptoAmount;

    private String paymentStatus;
    private boolean status;
    private boolean shown;

    // when?
    private long createdAt;
    private long updatedAt;

}
