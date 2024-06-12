package com.ndb.auction.service;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.InternalWalletDao;
import com.ndb.auction.dao.oracle.ShuftiDao;
import com.ndb.auction.dao.oracle.auction.AuctionAvatarDao;
import com.ndb.auction.dao.oracle.auction.AuctionDao;
import com.ndb.auction.dao.oracle.avatar.AvatarComponentDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileFactsDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileSetDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileSkillDao;
import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.dao.oracle.balance.FiatBalanceDao;
import com.ndb.auction.dao.oracle.other.BidDao;
import com.ndb.auction.dao.oracle.other.GeoLocationDao;
import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.presale.PreSaleConditionDao;
import com.ndb.auction.dao.oracle.presale.PreSaleDao;
import com.ndb.auction.dao.oracle.presale.PreSaleOrderDao;
import com.ndb.auction.dao.oracle.transaction.CoinsPaymentDao;
import com.ndb.auction.dao.oracle.transaction.DepositTransactionDao;
import com.ndb.auction.dao.oracle.transaction.StripeTransactionDao;
import com.ndb.auction.dao.oracle.transaction.WithdrawTransactionDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalAuctionDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeCustomerDao;
import com.ndb.auction.dao.oracle.user.UserAvatarDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.UserKybDao;
import com.ndb.auction.dao.oracle.user.UserSecurityDao;
import com.ndb.auction.dao.oracle.user.UserVerifyDao;
import com.ndb.auction.dao.oracle.verify.KycSettingDao;
import com.ndb.auction.models.TokenAsset;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.models.balance.FiatBalance;
import com.ndb.auction.schedule.BroadcastNotification;
import com.ndb.auction.schedule.ScheduledTasks;
import com.ndb.auction.service.payment.TxnFeeService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.SMSService;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.ndb.auction.web3.NDBCoinService;
import com.ndb.auction.web3.NdbWalletService;
import com.ndb.auction.web3.UserWalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class BaseService {

    private static final String HMAC_SHA_512 = "HmacSHA512";

    public static final String COINS_API_URL = "https://www.coinpayments.net/api.php";

    public final static String VERIFY_TEMPLATE = "verify.ftlh";
    public final static String _2FA_TEMPLATE = "2faEmail.ftlh";
    public final static String RESET_TEMPLATE = "reset.ftlh";
    public final static String NEW_USER_CREATED = "new_user.ftlh";

    @Value("${coinspayment.public.key}")
    public String COINSPAYMENT_PUB_KEY;

    @Value("${coinspayment.private.key}")
    public String COINSPAYMENT_PRIV_KEY;

    @Value("${coinspayment.ipn.secret}")
    public String COINSPAYMENT_IPN_SECRET;

    @Value("${coinspayment.ipn.url}")
    public String COINSPAYMENT_IPN_URL;

    protected static Gson gson = new Gson();
    
    protected WebClient coinPaymentAPI;

    @Autowired
    ScheduledTasks schedule;

    @Autowired
    public AuctionDao auctionDao;

    @Autowired
    public AuctionAvatarDao auctionAvatarDao;

    @Autowired
    public BidDao bidDao;

    @Autowired
    public UserDao userDao;

    @Autowired
    public UserAvatarDao userAvatarDao;

    @Autowired
    public UserKybDao userKybDao;

    @Autowired
    public UserSecurityDao userSecurityDao;

    @Autowired
    public UserVerifyDao userVerifyDao;

    @Autowired
    public NotificationDao notificationDao;

    @Autowired
    public MailService mailService;

    @Autowired
    public TotpService totpService;

    @Autowired
    public SMSService smsService;

    @Autowired
    public AvatarComponentDao avatarComponentDao;

    @Autowired
    public AvatarProfileDao avatarProfileDao;

    @Autowired
    public AvatarProfileSkillDao avatarSkillDao;

    @Autowired
    public AvatarProfileFactsDao avatarFactDao;

    @Autowired
    public AvatarProfileSetDao avatarSetDao;

    @Autowired
    public StripeTransactionDao stripeDao;

    @Autowired
    public NotificationService notificationService;

    @Autowired
    public UserWalletService userWalletService;

    @Autowired
    public NdbWalletService ndbWalletService;

    @Autowired
    public TokenAssetService tokenAssetService;

    @Autowired
    public GeoLocationDao geoLocationDao;

    @Autowired
    public TierService tierService;

    @Autowired
    public TierTaskService tierTaskService;

    @Autowired
    public TaskSettingService taskSettingService;

    @Autowired
    public BroadcastNotification broadcastNotification;

    @Autowired
    public CryptoBalanceDao balanceDao;

    @Autowired
    public InternalWalletDao walletDao;

    @Autowired
    public ShuftiDao shuftiDao;

    @Autowired
    public KycSettingDao kycSettingDao;

    @Autowired
    public PreSaleDao presaleDao;

    @Autowired
    public PreSaleConditionDao presaleConditionDao;

    @Autowired
    public DepositTransactionDao depositTxnDao;

    @Autowired
    public PreSaleOrderDao presaleOrderDao;

    @Autowired
    protected WithdrawTransactionDao withdrawTxnDao;

    @Autowired
    protected CoinsPaymentDao coinpaymentsDao;

    @Autowired
    protected FiatBalanceDao fiatBalanceDao;

    @Autowired
    protected NDBCoinService ndbCoinService;

    @Autowired
    protected ThirdAPIUtils thirdAPI;

    @Autowired
	protected FiatAssetService fiatAssetService;

    @Autowired
    protected ThirdAPIUtils apiUtils;

    @Autowired
    protected StripeCustomerDao stripeCustomerDao;

    @Autowired
    protected TxnFeeService txnFeeService;

    @Autowired
    protected PaypalAuctionDao paypalAuctionDao;

    public String buildHmacSignature(String value, String secret) {
        String result;
        try {
            Mac hmacSHA512 = Mac.getInstance(HMAC_SHA_512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA_512);
            hmacSHA512.init(secretKeySpec);

            byte[] digest = hmacSHA512.doFinal(value.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Problemas calculando HMAC", ex);
        }
        return result;
    }

    public Double getTotalBalance(int userId) {

        double totalBalance = 0.0;
        
        // calculate crypto balance
        List<CryptoBalance> cryptoBalances = balanceDao.selectByUserId(userId, null);
        for (CryptoBalance cryptoBalance : cryptoBalances) {
            TokenAsset asset = tokenAssetService.getTokenAssetById(cryptoBalance.getTokenId());
            double _price = thirdAPI.getCryptoPriceBySymbol(asset.getTokenSymbol());
            double _balance = _price * (cryptoBalance.getFree() + cryptoBalance.getHold());
            totalBalance += _balance;
        }

        List<FiatBalance> fiatBalances = fiatBalanceDao.selectByUserId(userId, null);
        for (FiatBalance fiatBalance : fiatBalances) {
            

        }

        return 0.0;
    }
}
