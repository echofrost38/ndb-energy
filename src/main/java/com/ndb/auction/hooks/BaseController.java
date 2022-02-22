package com.ndb.auction.hooks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.ndb.auction.dao.oracle.transaction.DepositTransactionDao;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.CryptoService;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.NotificationService;
import com.ndb.auction.service.PresaleOrderService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.TaskSettingService;
import com.ndb.auction.service.TierService;
import com.ndb.auction.service.TierTaskService;
import com.ndb.auction.service.TokenAssetService;
import com.ndb.auction.service.payment.CoinpaymentAuctionService;
import com.ndb.auction.service.user.UserAvatarService;
import com.ndb.auction.service.user.UserKybService;
import com.ndb.auction.service.user.UserSecurityService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.user.UserVerifyService;
import com.ndb.auction.web3.NDBCoinService;
import com.ndb.auction.web3.UserWalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BaseController {

    @Autowired
    CryptoService cryptoService;

    @Autowired
    BidService bidService;

    @Autowired
    UserService userService;

    @Autowired
    UserAvatarService userAvatarService;

    @Autowired
    UserKybService userKybService;

    @Autowired
    UserSecurityService userSecurityService;

    @Autowired
    UserVerifyService userVerifyService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserWalletService userWalletService;

    @Autowired
    TierService tierService;

    @Autowired
    TierTaskService tierTaskService;

    @Autowired
    TaskSettingService taskSettingService;
    
    @Autowired
    DepositTransactionDao depositTxnDao;

    @Autowired
    InternalBalanceService balanceService;

    @Autowired
    PresaleOrderService presaleOrderService;

    @Autowired
    TokenAssetService tokenAssetService;

    @Autowired
    protected NDBCoinService ndbCoinService;

    @Autowired
    protected PresaleService presaleService;

    @Autowired
    protected CoinpaymentAuctionService coinpaymentAuctionService;

    public static final String SHARED_SECRET = "a2282529-0865-4dbf-b837-d6f31db0e057";

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String HMAC_SHA_1 = "HmacSHA1";

    @Value("${coinspayment.merchant.id}")
    public String MERCHANT_ID;

    @Value("${coinspayment.public.key}")
    public String COINSPAYMENT_PUB_KEY;

    @Value("${coinspayment.private.key}")
    public String COINSPAYMENT_PRIV_KEY;

    @Value("${coinspayment.ipn.secret}")
    public String COINSPAYMENT_IPN_SECRET;

    @Value("${coinspayment.ipn.url}")
    public String COINSPAYMENT_IPN_URL;

    public String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public String buildHmacSignature(String value, String secret) {
        String result;
        try {
            Mac hmacSHA512 = Mac.getInstance(HMAC_SHA_256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA_256);
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

    public String buildHmacSHA1Signature(String value, String secret) {
        String result;
        try {
            Mac hmacSHA512 = Mac.getInstance(HMAC_SHA_1);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA_1);
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

    public double getDouble(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int getInt(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
    
    public long getLong(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
    
    public String getString(HttpServletRequest request, String param, Boolean trim) {
    	try {
    		String result = request.getParameter(param);
    		if(trim) {
    			result = result.trim();
    		}
    		return result;
    	} catch (Exception e) {
    		return null;
    	}
    }

}
