package com.ndb.auction.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    
    public Auction(int _round, String _startedAt, Long duration, Long _totalToken, Long _minPrice, List<AvatarSet> avatar, Long token) {
    	this.round = _round;
    	this.totalToken = _totalToken;
    	this.minPrice = _minPrice;
    	this.sold = 0L;
    	
    	// cast String date time to Long epoch
    	// Date Format : 2021-10-24T12:00:00.000-0000
    	// check null
    	if(_startedAt != null) {
	    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	    	try {
	    	    Date d = f.parse(_startedAt);
	    	    Long startedAtMill = d.getTime();
	    	    Long endedAtMill = startedAtMill + duration;
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
