package com.ndb.auction.service;

import com.ndb.auction.dao.AuctionDao;
import com.ndb.auction.dao.AvatarDao;
import com.ndb.auction.dao.BidDao;
import com.ndb.auction.dao.CryptoPaymentDao;
import com.ndb.auction.dao.NotificationDao;
import com.ndb.auction.dao.StripePaymentDao;
import com.ndb.auction.dao.UserDao;
import com.ndb.auction.schedule.ScheduledTasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BaseService {
	
    public final static String VERIFY_TEMPLATE = "verify.ftlh";
    public final static String _2FA_TEMPLATE = "2faEmail.ftlh";
    public final static String RESET_TEMPLATE = "reset.ftlh";
    
    @Value("${coinbase.apiKey}")
	public String coinbaseApiKey;
	
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
}
