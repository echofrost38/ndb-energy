package com.ndb.auction.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.bid.BidHoldings;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Bid {

    public static final int CREDIT = 1;
    public static final int CRYPTO = 2;
    public static final int WALLET = 3;
	
	public static final int NOT_CONFIRMED = 0;
	public static final int WINNER 		  = 1;
	public static final int FAILED 		  = 2;
	public static final int REJECTED	  = 3;
    public static final int INSUFFI 	  = 4; 
	
    private int userId;
    private int roundId;
    private Long tokenAmount;
    
    private Long totalPrice;
    private Long tokenPrice;

    private Long tempTokenAmount;
    private Long tempTokenPrice;
    private Long delta;
    private boolean pendingIncrease;

    private Map<String, BidHolding> holdingList;

    private int payType;
    private String cryptoType;
    private Long placedAt;
    private Long updatedAt; 
    private int status;
    
    public Bid(int userId, int roundId, Long tokenAmount, Long tokenPrice) {
    	this.userId = userId;
    	this.roundId = roundId;
    	this.tokenAmount = tokenAmount;
    	this.tokenPrice = tokenPrice;
    	this.totalPrice = tokenAmount * tokenPrice;
    	this.placedAt = new Date().getTime();
    	this.updatedAt = this.placedAt;
    	this.status = NOT_CONFIRMED;
        this.holdingList = new HashMap<>();
    }

    public List<BidHoldings> getHoldings() {
        List<BidHoldings> list = new ArrayList<>();
        Set<String> keys = holdingList.keySet();
        for (String keyString : keys) {
            BidHoldings holding = new BidHoldings();
            holding.setKey(keyString);
            holding.setValue(holdingList.get(keyString));
            list.add(holding);
        }
        return list;
    }
    
}
