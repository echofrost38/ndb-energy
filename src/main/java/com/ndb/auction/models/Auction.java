package com.ndb.auction.models;

import java.util.List;

import com.ndb.auction.models.avatar.AvatarSet;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Auction extends BaseModel {
	
	// Auction status constants
	public static final int PENDING   = 0;
    public static final int COUNTDOWN = 1;
	public static final int STARTED   = 2;
	public static final int ENDED     = 3;

    private int round;
    private Long startedAt;
    private Long endedAt;
    private Long totalToken;
    private Long minPrice;
    private Long sold;
    private AuctionStats stats;

    private List<AvatarSet> avatar;
    private Long token;

    private int status;
    
    public Auction(int _round, Long _startedAt, Long duration, Long _totalToken, Long _minPrice, List<AvatarSet> avatar, Long token) {
    	this.round = _round;
    	this.totalToken = _totalToken;
    	this.minPrice = _minPrice;
    	this.sold = 0L;
    	
    	Long startedAtMill = _startedAt;
		Long endedAtMill = startedAtMill + duration;
		this.startedAt = startedAtMill;
		this.endedAt = endedAtMill;

		// initial pending status
    	this.status = COUNTDOWN; 
    	AuctionStats auctionStats = new AuctionStats();
    	this.stats = auctionStats;
        this.avatar = avatar;
        this.token = token;
    }

}
