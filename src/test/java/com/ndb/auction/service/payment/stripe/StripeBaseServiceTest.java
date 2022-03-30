package com.ndb.auction.service.payment.stripe;

import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.WhitelistDao;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.Whitelist;
import com.ndb.auction.service.payment.TxnFeeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StripeBaseServiceTest {

    @Mock
    private WhitelistDao whitelistDao;
    @Mock
    private TxnFeeService txnFeeService;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private StripeBaseService stripeBaseService;

    private int userId;
    private double txnFee;
    private double whitelistFee;

    @Before
    public void setUp() {

        userId = 1;
        txnFee = 0.1;
        whitelistFee = 0.0;
        User user = new User();
        user.setId(userId);
        user.setTierLevel(5);
        Mockito.when(userDao.selectById(userId)).thenReturn(user);
        Mockito.when(txnFeeService.getFee(user.getTierLevel())).thenReturn(txnFee);
    }


    @Test
    public void stripeFeeTest() {

        Mockito.when(whitelistDao.selectByUserId(userId)).thenReturn(null);

        long amount = 100000;
        long fee = stripeBaseService.getStripeFee(userId, amount);
        long calculatedFee = (long) (amount * (stripeBaseService.getSTRIPE_FEE() + txnFee) / 100 + 30);
        Assert.assertEquals(calculatedFee, fee);
    }

    @Test
    public void stripeFeeWhitelistTest() {

        Mockito.when(whitelistDao.selectByUserId(userId)).thenReturn(new Whitelist());

        long amount = 100000;
        long fee = stripeBaseService.getStripeFee(userId, amount);
        long calculatedFee = (long) (amount * (stripeBaseService.getSTRIPE_FEE() + whitelistFee) / 100 + 30);
        Assert.assertEquals(calculatedFee, fee);
    }

    @Test
    public void totalAmountTest() {

        Mockito.when(whitelistDao.selectByUserId(userId)).thenReturn(null);

        long amount = 100000;
        long fee = stripeBaseService.getStripeFee(userId, amount);

        long totalAmount = stripeBaseService.getTotalAmount(userId, amount);
        Assert.assertEquals(amount+fee, totalAmount);
    }

}