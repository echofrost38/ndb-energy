package com.ndb.auction.hooks;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ndb.auction.exceptions.IPNExceptions;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.models.transaction.DepositTransaction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.Balance;

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
		
		// String reqQuery = "";
		// Enumeration<String> enumeration = request.getParameterNames();
        // while(enumeration.hasMoreElements()){
        //     String parameterName = (String) enumeration.nextElement();
        //     reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        // }
		
		// reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
		// reqQuery = reqQuery.replaceAll("@", "%40");
		// reqQuery = reqQuery.replace(' ', '+');
		// log.info("IPN reqQuery : {}", reqQuery);

        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
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
            CryptoTransaction txn = cryptoService.getTransactionById(id);
            User user = userService.getUserById(txn.getUserId());
    
            if(txn.getTransactionType() == CryptoTransaction.AUCTION) {
                Bid bid = bidService.getBid(txn.getRoundId(), txn.getUserId());
    
                if (bid.isPendingIncrease()) {
                    double pendingPrice = bid.getDelta();
                    if (pendingPrice > fiatAmount) {
                        
                        new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
    
                    bidService.updateBid(txn.getUserId(), txn.getRoundId(), bid.getTempTokenAmount(),
                            bid.getTempTokenPrice());
    
                } else {
                    long totalPrice = bid.getTotalPrice();
                    if (totalPrice > fiatAmount) {
    
                        new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
    
                    // update user tier points
                    List<Tier> tierList = tierService.getUserTiers();
                    TaskSetting taskSetting = taskSettingService.getTaskSetting();
                    TierTask tierTask = tierTaskService.getTierTask(bid.getUserId());
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
                bidService.updateBidRanking(txn.getUserId(), txn.getRoundId());
            
            } 
            cryptoService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, amount, cryptoType);
    
            // send notification to user for payment result!!
            notificationService.sendNotification(
                    txn.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    "You have successfully deposited " + amount + cryptoType + " for Auction Round.");
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

        // String reqQuery = "";
        // Enumeration<String> enumeration = request.getParameterNames();
        // while(enumeration.hasMoreElements()){
        //     String parameterName = (String) enumeration.nextElement();
        //     reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        // }

        // reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
        // reqQuery = reqQuery.replaceAll("@", "%40");
        // reqQuery = reqQuery.replace(' ', '+');
        // log.info("IPN reqQuery : {}", reqQuery);

        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            CryptoTransaction txn = cryptoService.getTransactionById(id);
            User user = userService.getUserById(txn.getUserId());

            PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(txn.getPresaleOrderId());
            Long totalPrice = presaleOrder.getNdbAmount() * presaleOrder.getNdbPrice();
            
            if(totalPrice > fiatAmount) {
                
                new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
    
            // processing order
            Long ndb = presaleOrder.getNdbAmount();
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
            double presalePoint = tierTask.getDirect();
            presalePoint += taskSetting.getDirect() * fiatAmount;
            tierTask.setDirect(presalePoint);
    
            double newPoint = user.getTierPoint() + taskSetting.getDirect() * fiatAmount;
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

            cryptoService.updateTransaction(txn.getId(), CryptoTransaction.CONFIRMED, amount, cryptoType);
    
            // send notification to user for payment result!!
            notificationService.sendNotification(
                    txn.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    "You have successfully deposited " + amount + cryptoType + " for Presale Round.");
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
		
		// String reqQuery = "";
		// Enumeration<String> enumeration = request.getParameterNames();
        // while(enumeration.hasMoreElements()){
        //     String parameterName = (String) enumeration.nextElement();
        //     reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        // }
		
		// reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
		// reqQuery = reqQuery.replaceAll("@", "%40");
		// reqQuery = reqQuery.replace(' ', '+');
		// log.info("IPN reqQuery : {}", reqQuery);

        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
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
            
            DepositTransaction txn = depositTxnDao.selectById(id);
            int userId = txn.getUserId();
            
            balanceService.addFreeBalance(userId, cryptoType, amount);

            List<Balance> balances = balanceService.getInternalBalances(userId);

            double totalBalance = 0.0;
            for (Balance balance : balances) {
                // get price and total balance
                double _price = cryptoService.getCryptoPriceBySymbol(balance.getTokenSymbol());
                double _balance = _price * (balance.getFree() + balance.getHold());
                totalBalance += _balance;
            }
    
            // update user tier points
            User user = userService.getUserById(userId);
            List<Tier> tierList = tierService.getUserTiers();
            TaskSetting taskSetting = taskSettingService.getTaskSetting();
            TierTask tierTask = tierTaskService.getTierTask(userId);
    
            if(tierTask.getWallet() < totalBalance) {
    
                tierTask.setWallet(totalBalance);
                // get point
                double gainedPoint = 0.0;
                for (WalletTask task : taskSetting.getWallet()) {
                    if(task.getAmount() < totalBalance) {
                        gainedPoint = task.getPoint();
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
                tierTaskService.updateTierTask(tierTask);
            }
    
            notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS, 
                "Deposit Successful", 
                String.format("You have successfully deposited %f %s", amount, cryptoType));
    
			depositTxnDao.updateStatus(id, currency, amount, fiatAmount);
	    } else if (status < 0) {
	        //payment error, this is usually final but payments will sometimes be reopened if there was no exchange rate conversion or with seller consent
	    } else {
	        //payment is pending, you can optionally add a note to the order page
	    }

        return new ResponseEntity<>(HttpStatus.OK); 
    }

}
