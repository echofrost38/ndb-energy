package com.ndb.auction.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private long startedAt;
    private long endedAt;
    private long totalToken;
    private long minPrice;
    private long sold;
    private AuctionStats stats;

    private List<AvatarSet> avatar;
    private long token;

    private int status;
    
    public Auction(int _round, String _startedAt, long duration, long _totalToken, long _minPrice, List<AvatarSet> avatar, long token) {
    	this.round = _round;
    	this.totalToken = _totalToken;
    	this.minPrice = _minPrice;
    	this.sold = 0;
    	
    	// cast String date time to Long epoch
    	// Date Format : 2021-10-24T12:00:00.000-0000
    	// check null
    	if(_startedAt != null) {
	    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	    	try {
	    	    Date d = f.parse(_startedAt);
	    	    long startedAtMill = d.getTime();
	    	    long endedAtMill = startedAtMill + duration;
	    	    this.startedAt = startedAtMill;
	    	    this.endedAt = endedAtMill;
	    	} catch (ParseException e) {
	    	    e.printStackTrace();
	    	}
    	}
    	// initial pending status
    	this.status = PENDING; 
    	AuctionStats auctionStats = new AuctionStats();
    	this.stats = auctionStats;
        this.avatar = avatar;
        this.token = token;
    }

}
