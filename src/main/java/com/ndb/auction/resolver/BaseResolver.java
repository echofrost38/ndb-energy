package com.ndb.auction.resolver;

import com.google.gson.Gson;
import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.AvatarService;
import com.ndb.auction.service.BaseVerifyService;
import com.ndb.auction.service.BidService;
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
import com.ndb.auction.service.TokenAssetService;
import com.ndb.auction.service.payment.PlaidService;
import com.ndb.auction.service.payment.TxnFeeService;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentAuctionService;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentPresaleService;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentWalletService;
import com.ndb.auction.service.payment.paypal.PaypalAuctionService;
import com.ndb.auction.service.payment.paypal.PaypalPresaleService;
import com.ndb.auction.service.payment.stripe.StripeAuctionService;
import com.ndb.auction.service.payment.stripe.StripeBaseService;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.payment.stripe.StripePresaleService;
import com.ndb.auction.service.payment.stripe.StripeWalletService;
import com.ndb.auction.service.user.UserSecurityService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.user.UserVerifyService;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.service.withdraw.PaypalWithdrawService;
import com.ndb.auction.utils.IPChecking;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.ndb.auction.web3.NDBCoinService;
import com.ndb.auction.web3.NdbWalletService;
import com.ndb.auction.web3.UserWalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;

public class BaseResolver {

    @Value("${website.url}")
	protected String WEBSITE_URL;
	protected final double PAYPAL_FEE = 5;

	protected static Gson gson = new Gson();

	@Value("${paypal.callbackUrl}")
    protected String PAYPAL_CALLBACK_URL;

	@Autowired
	protected AuctionService auctionService;

	@Autowired
	protected BidService bidService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected AuthenticationManager authenticationManager;

	@Autowired
	protected JwtUtils jwtUtils;

	@Autowired
	protected TotpService totpService;

	@Autowired
	protected AvatarService avatarService;

	@Autowired
	protected ProfileService profileService;

	@Autowired
	protected NotificationService notificationService;

	@Autowired
	protected StatService statService;

	@Autowired
	protected OAuth2RegistrationService oAuth2RegistrationService;

	@Autowired
	protected FinancialService financialService;

	@Autowired
	protected UserWalletService userWalletService;

	@Autowired
	protected NdbWalletService ndbWalletService;

	@Autowired
	protected IPChecking ipChecking;

	@Autowired
	protected KYBService kybService;

	@Autowired
	protected TokenAssetService tokenAssetService;

	@Autowired
	protected UserVerifyService userVerifyService;

	@Autowired
	protected UserSecurityService userSecurityService;

	@Autowired
	protected TierTaskService tierTaskService;

	@Autowired
	protected
	BaseVerifyService baseVerifyService;

	@Autowired
    protected
	InternalBalanceService internalBalanceService;

	@Autowired
	protected PresaleService presaleService;

	@Autowired
    protected ShuftiService shuftiService;

	@Autowired
	protected PresaleOrderService presaleOrderService;

	@Autowired
	protected NDBCoinService ndbCoinService;

	@Autowired
	protected FiatAssetService fiatAssetService;

	@Autowired
	protected PlaidService plaidService;

	@Autowired
	protected CoinpaymentAuctionService coinpaymentAuctionService;

	@Autowired
	protected CoinpaymentPresaleService	coinpaymentPresaleService;

	@Autowired
	protected CoinpaymentWalletService coinpaymentWalletService;

	@Autowired
	protected StripeAuctionService stripeAuctionService;

	@Autowired
	protected StripePresaleService stripePresaleService;

	@Autowired
	protected StripeBaseService stripeBaseService;

	@Autowired
	protected StripeWalletService stripeWalletService;

	@Autowired
	protected ThirdAPIUtils thirdAPIUtils;

	@Autowired
	protected TxnFeeService txnFeeService;

	@Autowired
	protected PaypalAuctionService paypalAuctionService;

	@Autowired
	protected StripeCustomerService stripeCustomerService;

	@Autowired
	protected PaypalWithdrawService paypalWithdrawService;

	@Autowired
	protected PaypalPresaleService paypalPresaleService;
}
