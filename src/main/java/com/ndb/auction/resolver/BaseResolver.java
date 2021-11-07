package com.ndb.auction.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.AvatarService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.ProfileService;
import com.ndb.auction.service.StripeService;
import com.ndb.auction.service.TotpService;
import com.ndb.auction.service.UserService;

public class BaseResolver {
	
    @Autowired
    AuctionService auctionService;
    
    @Autowired
    BidService bidService;
    
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	TotpService totpService;
    
	@Autowired
	StripeService stripeService;
	
	@Autowired
	AvatarService avatarService;

	@Autowired
	ProfileService profileService;
}
