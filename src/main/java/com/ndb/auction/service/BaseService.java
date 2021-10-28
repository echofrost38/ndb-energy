package com.ndb.auction.service;

import com.ndb.auction.dao.AuctionDao;
import com.ndb.auction.dao.BidDao;
import com.ndb.auction.dao.UserDao;
import com.ndb.auction.schedule.ScheduledTasks;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
	
	@Autowired
	ScheduledTasks schedule;
	
    @Autowired
    public AuctionDao auctionDao;
    
    @Autowired
    public BidDao bidDao;
    
    @Autowired
    public UserDao userDao;
}
