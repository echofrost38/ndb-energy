package com.ndb.auction.service;

import com.ndb.auction.dao.AuctionDao;
import com.ndb.auction.dao.AvatarDao;
import com.ndb.auction.dao.BidDao;
import com.ndb.auction.dao.CryptoPaymentDao;
import com.ndb.auction.dao.DirectSaleDao;
import com.ndb.auction.dao.GeoLocationDao;
import com.ndb.auction.dao.NotificationDao;
import com.ndb.auction.dao.StripePaymentDao;
import com.ndb.auction.dao.SumsubDao;
import com.ndb.auction.dao.UserDao;
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

	@Autowired
	ScheduledTasks schedule;

    @Autowired
    public AuctionDao auctionDao;
    
    @Autowired
    public BidDao bidDao;
    
    @Autowired
    public UserDao userDao;
    
    @Autowired
    public NotificationDao notificationDao;
    
    @Autowired
    public MailService mailService;
    
    @Autowired
    public TotpService totpService;
    
    @Autowired
    public SMSService smsService;
    
    @Autowired
    public AvatarDao avatarDao;
    
    @Autowired
    public StripePaymentDao stripeDao;

    @Autowired 
    public CryptoPaymentDao cryptoDao;
    
    @Autowired
    public NotificationService notificationService;

    @Autowired
    public SumsubDao sumsubDao;

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
}
