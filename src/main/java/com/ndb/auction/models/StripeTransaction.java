package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class StripeTransaction extends BaseModel {

	public static final Integer INITIATED = 0;
	public static final Integer AUTHORIZED = 1;
	public static final Integer CAPTURED = 2;
	public static final Integer CANCELED = 3;

	public static final int AUCTION  = 1;
	public static final int PRESALE  = 2;

	private int roundId;
	private int userId;
	private String paymentIntentId;
	private Long amount;
	private Integer status;
	private Long createdAt;
	private Long updatedAt;

	// 1 - Auction, 2 - Presale
	private int transactionType;

	public StripeTransaction(int roundId, int userId, Long amount, int txnType, String paymentIntentId) {
		this.roundId = roundId;
		this.userId = userId;
		this.amount = amount;
		this.paymentIntentId = paymentIntentId;
		this.createdAt = System.currentTimeMillis();
		this.status = AUTHORIZED;
		this.transactionType = txnType;
	}

}
