package com.ndb.auction.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.AvatarService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.CryptoService;
import com.ndb.auction.service.DirectSaleService;
import com.ndb.auction.service.FinancialService;
import com.ndb.auction.service.KYBService;
import com.ndb.auction.service.NotificationService;
import com.ndb.auction.service.OAuth2RegistrationService;
import com.ndb.auction.service.ProfileService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.StripeService;
import com.ndb.auction.service.TotpService;
import com.ndb.auction.service.UserService;
import com.ndb.auction.utils.IPChecking;
import com.ndb.auction.web3.NdbWalletService;
import com.ndb.auction.web3.UserWalletService;
import com.ndb.auction.service.SumsubService;

public class BaseResolver {

	@Autowired
	AuctionService auctionService;

	@Autowired
	BidService bidService;

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

	@Autowired
	CryptoService cryptoService;

	@Autowired
	SumsubService sumsubService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	StatService statService;

	@Autowired
	OAuth2RegistrationService oAuth2RegistrationService;

	@Autowired
	FinancialService financialService;

	@Autowired
	public DirectSaleService directSaleService;

	@Autowired
	UserWalletService userWalletService;

	@Autowired
	NdbWalletService ndbWalletService;

	@Autowired
	IPChecking ipChecking;

	@Autowired
	KYBService kybService;

}
