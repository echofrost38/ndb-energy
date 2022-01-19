package com.ndb.auction.service;

import com.google.gson.Gson;
import com.ndb.auction.background.TaskRunner;
import com.ndb.auction.dao.oracle.InternalBalanceDao;
import com.ndb.auction.dao.oracle.InternalWalletDao;
import com.ndb.auction.dao.oracle.ShuftiDao;
import com.ndb.auction.dao.oracle.TokenAssetDao;
import com.ndb.auction.dao.oracle.auction.AuctionAvatarDao;
import com.ndb.auction.dao.oracle.auction.AuctionDao;
import com.ndb.auction.dao.oracle.avatar.AvatarComponentDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileFactsDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileSetDao;
import com.ndb.auction.dao.oracle.avatar.AvatarProfileSkillDao;
import com.ndb.auction.dao.oracle.other.BidDao;
import com.ndb.auction.dao.oracle.other.CryptoTransactionDao;
import com.ndb.auction.dao.oracle.other.DirectSaleDao;
import com.ndb.auction.dao.oracle.other.GeoLocationDao;
import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.other.StripeTransactionDao;
import com.ndb.auction.dao.oracle.user.UserAvatarDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.UserKybDao;
import com.ndb.auction.dao.oracle.user.UserSecurityDao;
import com.ndb.auction.dao.oracle.user.UserVerifyDao;
import com.ndb.auction.dao.oracle.verify.KycSettingDao;
import com.ndb.auction.schedule.ScheduledTasks;
import com.ndb.auction.web3.NdbWalletService;
import com.ndb.auction.web3.UserWalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BaseService {

    @Value("${sumsub.secret.key}")
    public String SUMSUB_SECRET_KEY; // Example: Hej2ch71kG2kTd1iIUDZFNsO5C1lh5Gq

    @Value("${sumsub.app.token}")
    public String SUMSUB_APP_TOKEN; // Example: sbx:uY0CgwELmgUAEyl4hNWxLngb.0WSeQeiYny4WEqmAALEAiK2qTC96fBad

    public final static String VERIFY_TEMPLATE = "verify.ftlh";
    public final static String _2FA_TEMPLATE = "2faEmail.ftlh";
    public final static String RESET_TEMPLATE = "reset.ftlh";
    public final static String NEW_USER_CREATED = "new_user.ftlh";

    @Value("${coinbase.apiKey}")
    public String coinbaseApiKey;

    public final String SUMSUB_TEST_BASE_URL = "https://api.sumsub.com";

    protected static Gson gson = new Gson();

    @Autowired
    TaskRunner taskRunner;

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
    public CryptoTransactionDao cryptoTransactionDao;

    @Autowired
    public NotificationService notificationService;

    @Autowired
    public UserWalletService userWalletService;

    @Autowired
    public NdbWalletService ndbWalletService;

    @Autowired
    public DirectSaleDao directSaleDao;

    @Autowired
    public GeoLocationDao geoLocationDao;

    @Autowired
    public TierService tierService;

    @Autowired
    public TierTaskService tierTaskService;

    @Autowired
    public TaskSettingService taskSettingService;

    @Autowired
    public TokenAssetDao tokenAssetDao;

    @Autowired
    public InternalBalanceDao balanceDao;

    @Autowired
    public InternalWalletDao walletDao;

    @Autowired
    public ShuftiDao shuftiDao;

    @Autowired
    public KycSettingDao kycSettingDao;
}
