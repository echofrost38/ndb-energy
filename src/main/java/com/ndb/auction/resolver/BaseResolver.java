package com.ndb.auction.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import com.google.gson.Gson;
import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.AvatarService;
import com.ndb.auction.service.BaseVerifyService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.CryptoService;
import com.ndb.auction.service.FiatAssetService;
import com.ndb.auction.service.FinancialService;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.KYBService;
import com.ndb.auction.service.NotificationService;
import com.ndb.auction.service.OAuth2RegistrationService;
import com.ndb.auction.service.PresaleOrderService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.ProfileService;
import com.ndb.auction.service.ShuftiService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.TierTaskService;
import com.ndb.auction.service.payment.DepositService;
import com.ndb.auction.service.payment.PaypalService;
import com.ndb.auction.service.payment.StripeService;
import com.ndb.auction.service.user.UserSecurityService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.user.UserVerifyService;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.utils.IPChecking;
import com.ndb.auction.web3.NDBCoinService;
import com.ndb.auction.web3.NdbWalletService;
import com.ndb.auction.web3.UserWalletService;
import com.ndb.auction.service.TokenAssetService;

public class BaseResolver {

	protected static Gson gson = new Gson();

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
    protected
	StripeService stripeService;

	@Autowired
	AvatarService avatarService;

	@Autowired
	ProfileService profileService;

	@Autowired
    protected
	CryptoService cryptoService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	StatService statService;

	@Autowired
	OAuth2RegistrationService oAuth2RegistrationService;

	@Autowired
	FinancialService financialService;

	@Autowired
	UserWalletService userWalletService;

	@Autowired
	NdbWalletService ndbWalletService;

	@Autowired
	IPChecking ipChecking;

	@Autowired
	KYBService kybService;

	@Autowired
	TokenAssetService tokenAssetService;

	@Autowired
	UserVerifyService userVerifyService;

	@Autowired
	UserSecurityService userSecurityService;

	@Autowired
	TierTaskService tierTaskService;

	@Autowired
	BaseVerifyService baseVerifyService;

	@Autowired
	InternalBalanceService internalBalanceService;

	@Autowired
	protected PresaleService presaleService;

	@Autowired
	ShuftiService shuftiService;

	@Autowired
	DepositService depositService;

	@Autowired
	protected PresaleOrderService presaleOrderService;

	@Autowired
	protected NDBCoinService ndbCoinService;

	@Autowired
	protected FiatAssetService fiatAssetService;

}
