package com.ndb.auction.service;

import com.ndb.auction.dao.AuctionDao;
import com.ndb.auction.dao.BidDao;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
	
    @Autowired
    public AuctionDao auctionDao;
    
    @Autowired
    public BidDao bidDao;
}
