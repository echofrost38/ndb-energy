package com.ndb.auction.resolver;

import org.springframework.beans.factory.annotation.Autowired;

import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.BidService;

public class BaseResolver {
	
    @Autowired
    AuctionService auctionService;
    
    @Autowired
    BidService bidService;
    
}
