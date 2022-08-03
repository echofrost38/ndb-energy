package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.nyyupay.NyyuPayResponse;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.CryptoTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.user.WhitelistService;
import com.ndb.auction.utils.ThirdAPIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class NyyuPayController extends BaseController{

    @Autowired
    private WhitelistService whitelistService;

    @Autowired
    private ThirdAPIUtils thirdAPIUtils;

    private final double NYYUPAY_FEE = 0;

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;

    CryptoBalanceDao balanceDao;
    @Autowired
    public NyyuPayController(CryptoBalanceDao balanceDao){
        this.balanceDao=balanceDao;
    }
    @PostMapping("/nyyupay")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        String reqQuery;
        Map<String,String> reqHeader;
        try {
            reqQuery = getBody(request);
            reqHeader= getHeadersInfo(request);
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);
            String payload = reqHeader.get("x-auth-ts") +"POST"+reqQuery.toString();
            String hmac = buildHmacSignature(payload, PRIVATE_KEY);
            if (reqHeader.get("x-auth-key").equals(PUBLIC_KEY) && reqHeader.get("x-auth-token").equals(hmac)){
                System.out.println("NYYU PAY CALLBACK BODY: " + reqQuery);
                //New deposit
                int tokenId = tokenAssetService.getTokenIdBySymbol(response.getToken());
                int userId = nyyuWalletService.selectByAddress(response.getAddress()).getUserId();
                int decimal = response.getDecimal();
                double amount = response.getAmount().doubleValue()/Math.pow(10,decimal);
                balanceDao.addFreeBalance(userId, tokenId, amount);
                System.out.println("Deposit detection : " + reqQuery.toString());
                return response;
            } else
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/nyyupay/presale/{id}")
    @ResponseBody
    public ResponseEntity<?> NyyuPayPresaleCallbackHander(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            String reqQuery;
            Map<String,String> reqHeader;
            reqQuery = getBody(request);
            reqHeader= getHeadersInfo(request);
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);
            String payload = reqHeader.get("x-auth-ts") +"POST"+reqQuery.toString();
            String hmac = buildHmacSignature(payload, PRIVATE_KEY);
            if (reqHeader.get("x-auth-key").equals(PUBLIC_KEY) && reqHeader.get("x-auth-token").equals(hmac)) {
                System.out.println("NYYU PAY PRESALE CALLBACK BODY: " + reqQuery);

                String cryptoType = response.getToken();
                int decimal = response.getDecimal();
                double amount = response.getAmount().doubleValue()/Math.pow(10,decimal);
                var cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
                var fiatAmount = amount * cryptoPrice; // calculator fiatAmount based amount
                var txn = coinpaymentPresaleService.selectById(id);
                PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(txn.getOrderId());

                // checking balance
                var ndbToken = presaleOrder.getNdbAmount();
                var ndbPrice = presaleOrder.getNdbPrice();
                var totalPrice = ndbToken * ndbPrice;
                var totalOrder = getTotalOrder(presaleOrder.getUserId(), totalPrice);

                if(totalOrder > fiatAmount) {
                    log.info("total order: {}", totalPrice);
                    notificationService.sendNotification(
                            presaleOrder.getUserId(),
                            Notification.DEPOSIT_SUCCESS,
                            "PAYMENT CONFIRMED",
                            "Your purchase of " + ndbToken + "NDB" + " in the presale round was successful.");
                    var price = apiUtil.getCryptoPriceBySymbol("USDT");
                    log.info("added free balance: {}", fiatAmount / price);
                    balanceService.addFreeBalance(presaleOrder.getUserId(), cryptoType, fiatAmount / price);
                    return new ResponseEntity<>(HttpStatus.OK);
                }

                var overflow = (fiatAmount - totalOrder)/(fiatAmount/amount);
                balanceService.addFreeBalance(presaleOrder.getUserId(), cryptoType, overflow);

                presaleService.handlePresaleOrder(presaleOrder.getUserId(), id, totalOrder, "CRYPTO", presaleOrder);
                coinpaymentPresaleService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, amount, cryptoType);

                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Double getTotalOrder(int userId, double totalPrice) {
        User user = userService.getUserById(userId);
        Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());

        var white = whitelistService.selectByUser(userId);
        if(white != null) tierFeeRate = 0.0;

        return 100 * totalPrice / (100 - NYYUPAY_FEE - tierFeeRate);
    }

    private double getNyyuPayFee(int userId, double totalPrice) {
        User user = userService.getUserById(userId);
        Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());

        var white = whitelistService.selectByUser(userId);
        if(white != null) tierFeeRate = 0.0;
        return totalPrice * (NYYUPAY_FEE + tierFeeRate) / 100.0;
    }
}
