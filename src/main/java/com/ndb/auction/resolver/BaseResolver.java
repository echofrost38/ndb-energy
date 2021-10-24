package com.ndb.auction.resolver;

import org.springframework.beans.factory.annotation.Autowired;

import com.ndb.auction.service.AuctionService;

public class BaseResolver {
    @Autowired
    AuctionService auctionService;
}
