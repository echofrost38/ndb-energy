package com.ndb.auction.hooks;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ndb.auction.exceptions.IPNExceptions;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.CryptoTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.service.user.WhitelistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TODOs
 * 1. processing lack of payment!!!
 */

@RestController
@RequestMapping("/")
@Slf4j
public class CryptoController extends BaseController {

    @Autowired
    private WhitelistService whitelistService;

    private final double COINPAYMENT_FEE = 0.5;

    @PostMapping("/ipn/bid/{id}")
    @ResponseBody
    public Object coinPaymentsBidIpn(@PathVariable("id") int id, HttpServletRequest request) {

        // ipn mode check
		String ipnMode = request.getParameter("ipn_mode");
		if(!ipnMode.equals("hmac")) {
			log.error("IPN Mode is not HMAC");
			throw new IPNExceptions("IPN Mode is not HMAC");
		}
		
		String hmac = request.getHeader("hmac");
		if(hmac == null) {
			log.error("IPN Hmac is null");
			throw new IPNExceptions("IPN Hmac is null");
		}
		
		String merchant = request.getParameter("merchant");
		if(merchant == null || !merchant.equals(MERCHANT_ID)) {
			log.error("No or incorrect Merchant ID passed");
			throw new IPNExceptions("No or incorrect Merchant ID passed");
		}
		
		String reqQuery = "";
		Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        }
		
		reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
		reqQuery = reqQuery.replaceAll("@", "%40");
		reqQuery = reqQuery.replace(' ', '+');
		log.info("IPN reqQuery : {}", reqQuery);
		
		String _hmac = buildHmacSignature(reqQuery, COINSPAYMENT_IPN_SECRET);
		if(!_hmac.equals(hmac)) {
			throw new IPNExceptions("not match");
		}
        
		// get currency and amount
        String currency = getString(request, "currency", true);
        String cryptoType = currency.split(".")[0];
        Double amount = getDouble(request, "amount");
        Double fiatAmount = getDouble(request, "fiat_amount");
		
		int status = getInt(request, "status");
		log.info("IPN status : {}", status);

        if (status >= 100 || status == 2) {
            CoinpaymentAuctionTransaction txn = (CoinpaymentAuctionTransaction) coinpaymentAuctionService.selectById(id);
            User user = userService.getUserById(txn.getUserId());
    
            Bid bid = bidService.getBid(txn.getAuctionId(), txn.getUserId());
    
            if (bid.isPendingIncrease()) {
                double pendingPrice = bid.getDelta();
                if (pendingPrice > fiatAmount) {
                    new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }
                bidService.increaseAmount(
                    bid.getUserId(), bid.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice()
                );
                bid.setTokenAmount(bid.getTempTokenAmount());
                bid.setTokenPrice(bid.getTempTokenPrice());
                bid.setTotalAmount((double) (bid.getTokenAmount() * bid.getTokenPrice()));
            } else {
                Double totalAmount = bid.getTotalAmount();
                Double totalOrder = getTotalOrder(bid.getUserId(), totalAmount);
                // Double alreadyPaid = bid.getPaidAmount();
                // bidService.updatePaid(bid.getUserId(), bid.getRoundId(), fiatAmount);
                
                if (totalOrder > fiatAmount) {
                    new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }

                // update user tier points
                List<Tier> tierList = tierService.getUserTiers();
                TaskSetting taskSetting = taskSettingService.getTaskSetting();
                TierTask tierTask = tierTaskService.getTierTask(bid.getUserId());

                if(tierTask == null) {
                    tierTask = new TierTask(bid.getUserId());
                    tierTaskService.updateTierTask(tierTask);
                }

                List<Integer> auctionList = tierTask.getAuctions();
                if(!auctionList.contains(bid.getRoundId())) {
                    auctionList.add(bid.getRoundId());
                    // get point
                    double newPoint = user.getTierPoint() + taskSetting.getAuction();
                    int tierLevel = 0;

                    // check change in level
                    for (Tier tier : tierList) {
                        if(tier.getPoint() <= newPoint) {
                            tierLevel = tier.getLevel();
                        }
                    }
                    userService.updateTier(user.getId(), tierLevel, newPoint);
                    tierTaskService.updateTierTask(tierTask);
                } 
            }
            
            // Change crypto Hold amount!!!!!!!!!!!!!!!
            balanceService.addHoldBalance(txn.getUserId(), cryptoType, Double.valueOf(amount));
            bid.setPayType(Bid.CRYPTO);
            bidService.updateBidRanking(bid);

            coinpaymentAuctionService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, amount, cryptoType);
    
            // send notification to user for payment result!!
            var msg = "";
            if(cryptoType.equals("USDT") || cryptoType.equals("USDC")) {
                var df = new DecimalFormat("#.00");
                msg = "Your deposit of " + df.format(amount) + cryptoType + " for the auction round was successful.";
            } else {
                var df = new DecimalFormat("#.00000000");
                msg = "Your deposit of " + df.format(amount) + cryptoType + " for the auction round was successful.";
            }
            notificationService.sendNotification(
                    txn.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    msg);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/ipn/presale/{id}")
    @ResponseBody
    public ResponseEntity<?> coinPaymentsPresaleIpn(@PathVariable("id") int id, HttpServletRequest request) {
        
        // ipn mode check
        String ipnMode = request.getParameter("ipn_mode");
        if(!ipnMode.equals("hmac")) {
            log.error("IPN Mode is not HMAC");
            throw new IPNExceptions("IPN Mode is not HMAC");
        }

        String hmac = request.getHeader("hmac");
        if(hmac == null) {
            log.error("IPN Hmac is null");
            throw new IPNExceptions("IPN Hmac is null");
        }

        String merchant = request.getParameter("merchant");
        if(merchant == null || !merchant.equals(MERCHANT_ID)) {
            log.error("No or incorrect Merchant ID passed");
            throw new IPNExceptions("No or incorrect Merchant ID passed");
        }

        String reqQuery = "";
        Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        }

        reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
        reqQuery = reqQuery.replaceAll("@", "%40");
        reqQuery = reqQuery.replace(' ', '+');
        log.info("IPN reqQuery : {}", reqQuery);

        String _hmac = buildHmacSignature(reqQuery, COINSPAYMENT_IPN_SECRET);
        if(!_hmac.equals(hmac)) {
            throw new IPNExceptions("not match");
        }

        // get currency and amount
        String currency = getString(request, "currency", true);
        String cryptoType = currency.split(".")[0];
        Double amount = getDouble(request, "amount");
        Double fiatAmount = getDouble(request, "fiat_amount");

        int status = getInt(request, "status");
        log.info("IPN status : {}", status);

        if (status >= 100 || status == 2) {

            CoinpaymentPresaleTransaction txn = (CoinpaymentPresaleTransaction) coinpaymentPresaleService.selectById(id);
            User user = userService.getUserById(txn.getUserId());

            PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(txn.getOrderId());
            double totalPrice = presaleOrder.getNdbAmount() * presaleOrder.getNdbPrice();
            Double totalOrder = getTotalOrder(txn.getUserId(), totalPrice);

            if(totalOrder > fiatAmount) {
                new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
    
            // processing order
            double ndb = presaleOrder.getNdbAmount();
            if(presaleOrder.getDestination() == PreSaleOrder.INTERNAL) {
                balanceService.addFreeBalance(user.getId(), "NDB", Double.valueOf(ndb));
            } else if (presaleOrder.getDestination() == PreSaleOrder.EXTERNAL) {
                // transfer ndb
                ndbCoinService.transferNDB(txn.getUserId(), presaleOrder.getExtAddr(), Double.valueOf(ndb));
            }
    
            // update user tier points
            List<Tier> tierList = tierService.getUserTiers();
            TaskSetting taskSetting = taskSettingService.getTaskSetting();
            TierTask tierTask = tierTaskService.getTierTask(user.getId());
            
            if(tierTask == null) {
                tierTask = new TierTask(user.getId());
                tierTaskService.updateTierTask(tierTask);
            }
            
            double presalePoint = tierTask.getDirect();
            presalePoint += taskSetting.getDirect() * totalPrice;
            tierTask.setDirect(presalePoint);
    
            double newPoint = user.getTierPoint() + taskSetting.getDirect() * totalPrice;
            int tierLevel = 0;
    
            // check change in level
            for (Tier tier : tierList) {
                if(tier.getPoint() <= newPoint) {
                    tierLevel = tier.getLevel();
                }
            }
            userService.updateTier(user.getId(), tierLevel, newPoint);
            tierTaskService.updateTierTask(tierTask);
            presaleOrderService.updateStatus(presaleOrder.getId());
            presaleService.addSoldAmount(presaleOrder.getPresaleId(), ndb);

            coinpaymentPresaleService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, amount, cryptoType);
    
            // send notification to user for payment result!!
            notificationService.sendNotification(
                    txn.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    "Your deposit of " + amount + cryptoType + " for the presale round was successful.");
        }


        return null;
    }

    @PostMapping("/ipn/deposit/{id}")
    @ResponseBody
    public ResponseEntity<?> coinPaymentsDepositIpn(@PathVariable("id") int id, HttpServletRequest request) {
        // ipn mode check
		String ipnMode = request.getParameter("ipn_mode");
		if(!ipnMode.equals("hmac")) {
			log.error("IPN Mode is not HMAC");
			throw new IPNExceptions("IPN Mode is not HMAC");
		}
		
		String hmac = request.getHeader("hmac");
		if(hmac == null) {
			log.error("IPN Hmac is null");
			throw new IPNExceptions("IPN Hmac is null");
		}
		
		String merchant = request.getParameter("merchant");
		if(merchant == null || !merchant.equals(MERCHANT_ID)) {
			log.error("No or incorrect Merchant ID passed");
			throw new IPNExceptions("No or incorrect Merchant ID passed");
		}
		
		String reqQuery = "";
		Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        }
		
		reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
		reqQuery = reqQuery.replaceAll("@", "%40");
		reqQuery = reqQuery.replace(' ', '+');
		log.info("IPN reqQuery : {}", reqQuery);
		
		String _hmac = buildHmacSignature(reqQuery, COINSPAYMENT_IPN_SECRET);
        log.info("_hmac calculated: {}", _hmac);
        log.info("hmac in header: {}", hmac);
		// if(!_hmac.equals(hmac)) {
		// 	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		// }
        
		// get currency and amount
        String currency = getString(request, "currency", true);
        String cryptoType = "";
        if(currency.contains(".")) {
            cryptoType = currency.split(".")[0];
        } else {
            cryptoType = currency;
        }

        Double amount = getDouble(request, "amount");
        // Double fiatAmount = getDouble(request, "fiat_amount");
		
		int status = getInt(request, "status");
		log.info("IPN status : {}", status);
		if (status >= 100 || status == 2) {
            
            CoinpaymentWalletTransaction txn = (CoinpaymentWalletTransaction) coinpaymentWalletService.selectById(id);
            if(txn == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }

            if(!txn.getStatus()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            }

            int userId = txn.getUserId();

            // account for fee
            double fee = getCoinpaymentFee(userId, amount);
            double deposited = amount - fee;
            // update coinpayment deposit transaction
            coinpaymentWalletService.updateStatus(txn.getId(), 1, deposited, fee, cryptoType);

            balanceService.addFreeBalance(userId, cryptoType, deposited);
            List<BalancePayload> balances = balanceService.getInternalBalances(userId);

            double totalBalance = 0.0;
            for (BalancePayload balance : balances) {
                // get price and total balance
                double _price = apiUtil.getCryptoPriceBySymbol(balance.getTokenSymbol());
                double _balance = _price * (balance.getFree() + balance.getHold());
                totalBalance += _balance;
            }
    
            // update user tier points
            User user = userService.getUserById(userId);
            List<Tier> tierList = tierService.getUserTiers();
            TaskSetting taskSetting = taskSettingService.getTaskSetting();
            TierTask tierTask = tierTaskService.getTierTask(userId);

            if(tierTask == null) {
                tierTask = new TierTask(userId);
                tierTaskService.updateTierTask(tierTask);
            }
    
            if(tierTask.getWallet() < totalBalance) {
    
                tierTask.setWallet(totalBalance);
                // get point
                double gainedPoint = 0.0;
                for (WalletTask task : taskSetting.getWallet()) {
                    if(tierTask.getWallet() > task.getAmount()) {
                        continue;
                    }                    
                    if(totalBalance < task.getAmount()) {
                        break;
                    }
                    gainedPoint += task.getPoint();
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
                tierTaskService.updateTierTask(tierTask);
            }
    
            notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS, 
                "PAYMENT CONFIRMED", 
                String.format("Your deposit of %f %s was successful.", amount, cryptoType));
    
	    } else if (status < 0) {
	        //payment error, this is usually final but payments will sometimes be reopened if there was no exchange rate conversion or with seller consent
	    } else {
	        //payment is pending, you can optionally add a note to the order page
	    }

        return new ResponseEntity<>(HttpStatus.OK); 
    }

    private Double getTotalOrder(int userId, double totalPrice) {
        User user = userService.getUserById(userId);
        Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());

        var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;
        
        return 100 * totalPrice / (100 - COINPAYMENT_FEE - tierFeeRate);
    }

    private double getCoinpaymentFee(int userId, double totalPrice) {
        User user = userService.getUserById(userId);
        Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());

        var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;
        return totalPrice * (COINPAYMENT_FEE + tierFeeRate) / 100.0;
    }

}
