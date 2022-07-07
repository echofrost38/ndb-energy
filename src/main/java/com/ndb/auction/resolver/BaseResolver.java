package com.ndb.auction.resolver;

import java.util.Random;

import com.google.gson.Gson;
import com.ndb.auction.models.user.User;
import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.AvatarService;
import com.ndb.auction.service.BaseVerifyService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.FiatAssetService;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.KYBService;
import com.ndb.auction.service.NotificationService;
import com.ndb.auction.service.OAuth2RegistrationService;
import com.ndb.auction.service.PresaleOrderService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.ShuftiService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.TierTaskService;
import com.ndb.auction.service.TokenAssetService;
import com.ndb.auction.service.payment.PlaidService;
import com.ndb.auction.service.payment.TxnFeeService;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentWalletService;
import com.ndb.auction.service.user.UserReferralService;
import com.ndb.auction.service.user.UserSecurityService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.user.UserVerifyService;
import com.ndb.auction.service.user.WhitelistService;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.utils.IPChecking;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.ndb.auction.web3.NDBCoinService;
import com.ndb.auction.web3.NdbWalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;

public class BaseResolver {

	private final double COINPAYMENT_FEE = 0.5;

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
	protected UserReferralService referralService;

	@Autowired
	protected AuthenticationManager authenticationManager;

	@Autowired
	protected JwtUtils jwtUtils;

	@Autowired
	protected TotpService totpService;

	@Autowired
	protected AvatarService avatarService;

	@Autowired
	protected NotificationService notificationService;

	@Autowired
	protected StatService statService;

	@Autowired
	protected OAuth2RegistrationService oAuth2RegistrationService;

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
    protected InternalBalanceService internalBalanceService;

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
	protected CoinpaymentWalletService coinpaymentWalletService;

	@Autowired
	protected ThirdAPIUtils thirdAPIUtils;

	@Autowired
	protected TxnFeeService txnFeeService;

	@Autowired
	protected WhitelistService whitelistService;

	@Autowired
	protected MessageSource messageSource;

	protected double getPaypalFee(int userId, double amount) {
		User user = userService.getUserById(userId);
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;
		return amount * (PAYPAL_FEE + tierFeeRate) / 100 + 0.3;
	}

	protected double getPaypalWithdrawFee(int userId, double amount) {
		User user = userService.getUserById(userId);
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;
		return amount * tierFeeRate / 100 + 0.3;
	}

	protected double getTierFee(int userId, double amount) {
		User user = userService.getUserById(userId);
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;
		return amount * tierFeeRate / 100;
	}

	protected String getBankUID() {
		Random rnd = new Random();
		int number = rnd.nextInt(999999999);
		return String.format("%06d", number);
	}

	public Double getTotalCoinpaymentOrder(int userId, double totalPrice) {
        User user = userService.getUserById(userId);
        Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());

		var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;

        return 100 * totalPrice / (100 - COINPAYMENT_FEE - tierFeeRate);
    }
}
