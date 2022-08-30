package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.nyyupay.NyyuPayResponse;

import lombok.RequiredArgsConstructor;

import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.CryptoTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.payment.coinpayment.CoinpaymentWalletService;
import com.ndb.auction.service.user.WhitelistService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.utils.ThirdAPIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class NyyuPayController extends BaseController{

    @Value("${nyyupay.pubKey}")
    private String NYYU_PAY_PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String NYYU_PAY_PRIVATE_KEY;

    private final WhitelistService whitelistService;
    private final CoinpaymentWalletService coinpaymentWalletService;
    private final InternalBalanceService balanceService;
    private final ThirdAPIUtils thirdAPIUtils;
    private final MailService mailService;

    private final double NYYUPAY_FEE = 0;

    private boolean securityChecker(String reqQuery, Map<String, String> header) {
        String payload = header.get("x-auth-ts") + "POST" + reqQuery.toString();
        String hmac = buildHmacSignature(payload, NYYU_PAY_PRIVATE_KEY);
        if(!header.get("x-auth-key").equals(NYYU_PAY_PUBLIC_KEY) || !header.get("x-auth-token").equals(hmac))
            return false;
        return true;
    }

    @PostMapping("/nyyupay")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        String reqQuery;
        Map<String,String> reqHeader;
        try {
            reqQuery = getBody(request);
            reqHeader = getHeadersInfo(request);
            if(!securityChecker(reqQuery, reqHeader)) new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            
            log.info("NYYU PAY CALLBACK BODY: " + reqQuery);
            
            //New deposit
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);

            int decimal = response.getDecimal();
            var cryptoType = response.getToken();
            var depositAddress = response.getAddress();
            if(depositAddress == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            var nyyuWallet = nyyuWalletService.selectByAddress(depositAddress);
            if(nyyuWallet == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            int userId = nyyuWallet.getUserId();
            var user = userService.getUserById(userId);
            // deposited crypto amount
            double cryptoAmount = response.getAmount().doubleValue()/Math.pow(10,decimal);
            
            // fee
            var fee = cryptoType.equals("NDB") ? 0 : getTierFee(user, cryptoAmount);
            var cryptoPrice = cryptoType.equals("NDB") ? 0 : thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
            var deposited = cryptoAmount - fee;
            var amount = deposited * cryptoPrice;

            var m = new CoinpaymentDepositTransaction(0, userId, amount, cryptoAmount, fee, "DEPOSIT", cryptoType, "BEP20", "Nyyu.pay");
            m.setDepositStatus(1);
            coinpaymentWalletService.createNewDepositTxn(m);

            var balances = balanceService.getInternalBalances(userId);

            double totalBalance = 0.0;
            for (BalancePayload balance : balances) {
                // get price and total balance
                double _price = apiUtil.getCryptoPriceBySymbol(balance.getTokenSymbol());
                double _balance = _price * (balance.getFree() + balance.getHold());
                totalBalance += _balance;
            }

            var tierList = tierService.getUserTiers();
            var taskSetting = taskSettingService.getTaskSetting();
            var tierTask = tierTaskService.getTierTask(userId);

            if(tierTask == null) {
                tierTask = new TierTask(userId);
                tierTaskService.updateTierTask(tierTask);
            }
    
            if(tierTask.getWallet() < totalBalance) {
    
                // get point
                double gainedPoint = 0.0;
                for (WalletTask task : taskSetting.getWallet()) {
                    if(tierTask.getWallet() > task.getAmount()) continue;
                    if(totalBalance > task.getAmount()) {
                        // add point
                        gainedPoint += task.getPoint();
                    } else {
                        break;
                    }
                }
    
                double newPoint = user.getTierPoint() + gainedPoint;
                int tierLevel = 0;
                // check change in level
                for (Tier tier : tierList) {
                    if(tier.getPoint() <= newPoint) {
                        tierLevel = tier.getLevel();
                    }
                }
                userService.updateTier(user.getId(), tierLevel, newPoint);
                tierTask.setWallet(totalBalance);
                tierTaskService.updateTierTask(tierTask);
            }

            balanceService.addFreeBalance(userId, cryptoType, deposited);
            log.info("Deposit detection : " + reqQuery.toString());

            if(!cryptoType.equals("NDB")) {
                var admins = userService.getUsersByRole("ROLE_SUPER");
                try {
                    mailService.sendDeposit(
                        user.getEmail(), 
                        user.getAvatar().getPrefix() + " " + user.getAvatar().getName(),
                        "Nyyu payments", 
                        cryptoType, 
                        cryptoType, 
                        cryptoAmount, 
                        deposited, 
                        fee, 
                        admins
                    );
                } catch (Exception e) {
                    log.info("cannot send crypto deposit notification email to admin");
                }

                notificationService.sendNotification(
                    userId,
                    Notification.DEPOSIT_SUCCESS, 
                    "PAYMENT CONFIRMED", 
                    String.format("Your deposit of %f %s was successful.", cryptoAmount, cryptoType));
            }
    
            return new ResponseEntity<>(HttpStatus.OK); 
                
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/nyyupay/presale/{id}")
    @ResponseBody
    public ResponseEntity<?> NyyuPayPresaleCallbackHander(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            String reqQuery = getBody(request);;
            Map<String,String> reqHeader = getHeadersInfo(request);
            if(!securityChecker(reqQuery, reqHeader)) new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            log.info("NYYU PAY PRESALE CALLBACK BODY: " + reqQuery);
            
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);
            
            var depositAddress = response.getAddress();
            var nyyuWallet = nyyuWalletService.selectByAddress(depositAddress);
            if(nyyuWallet == null) new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            
            var cryptoType = response.getToken();
            int decimal = response.getDecimal();
            
            double cryptoAmount = response.getAmount().doubleValue() / Math.pow(10, decimal);
            var cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
            var fiatAmount = cryptoAmount * cryptoPrice; // calculator fiatAmount based amount

            var txn = coinpaymentPresaleService.selectById(id);
            if(txn == null || txn.getDepositStatus() != 0) new ResponseEntity<>(HttpStatus.BAD_REQUEST); 

            var presaleOrder = presaleOrderService.getPresaleById(txn.getOrderId());

            // checking balance
            var ndbToken = presaleOrder.getNdbAmount();
            var ndbPrice = presaleOrder.getNdbPrice();
            var totalPrice = ndbToken * ndbPrice;
            var totalOrder = getTotalOrder(presaleOrder.getUserId(), totalPrice);

            if(totalOrder > fiatAmount) {
                notificationService.sendNotification(
                        presaleOrder.getUserId(),
                        Notification.DEPOSIT_SUCCESS,
                        "PAYMENT CONFIRMED",
                        "Your purchase of " + ndbToken + "NDB" + " in the presale round was failed.");
                var price = apiUtil.getCryptoPriceBySymbol("USDT");
                log.info("added free balance: {}", fiatAmount / price);
                balanceService.addFreeBalance(presaleOrder.getUserId(), "USDT", fiatAmount / price);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            var overflow = (fiatAmount - totalOrder) / cryptoPrice;
            balanceService.addFreeBalance(presaleOrder.getUserId(), "USDT", overflow);

            presaleService.handlePresaleOrder(presaleOrder.getUserId(), id, totalOrder, "CRYPTO", presaleOrder);
            coinpaymentPresaleService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, cryptoAmount, cryptoType);

            return new ResponseEntity<>(HttpStatus.OK);
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

}
