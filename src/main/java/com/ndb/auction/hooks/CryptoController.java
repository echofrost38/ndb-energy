package com.ndb.auction.hooks;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.models.transaction.DepositTransaction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.coinbase.CoinbaseEvent;
import com.ndb.auction.models.coinbase.CoinbaseEventBody;
import com.ndb.auction.models.coinbase.CoinbaseEventData;
import com.ndb.auction.models.coinbase.CoinbasePayments;
import com.ndb.auction.models.coinbase.CoinbasePricing;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.Balance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODOs
 * 1. processing lack of payment!!!
 */

@RestController
@RequestMapping("/")
public class CryptoController extends BaseController {

    @PostMapping("/coinbase")
    @ResponseBody
    public Object CoinbaseWebhooks(HttpServletRequest request) {

        String hmac = request.getHeader("X-CC-Webhook-Signature");

        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String _hmac = buildHmacSignature(reqQuery, SHARED_SECRET);

        if (!hmac.equals(_hmac)) {
            return null;
        }
        CoinbaseEvent event = new Gson().fromJson(reqQuery, CoinbaseEvent.class);
        CoinbaseEventBody body = event.getEvent();

        if (body.getType().equals("charge:confirmed")) {
            CoinbaseEventData data = body.getData();

            if (data.getPayments().size() == 0) {
                return false;
            }
            CoinbasePayments payments = data.getPayments().get(0);

            if (payments.getStatus().equals("CONFIRMED")) {
                String code = data.getCode();

                CryptoTransaction txn = cryptoService.getTransactionByCode(code);

                // check round payment
                if(txn != null) {
                    if(handleRoundPayment(payments, txn)) {
                        return new ResponseEntity<>(HttpStatus.OK);
                    } 
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } 

                DepositTransaction depositTxn = depositTxnDao.selectByCode(code);
                if(depositTxn != null) {
                    if(handleDepositPayment(payments, depositTxn)) {
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean handleRoundPayment(CoinbasePayments payment, CryptoTransaction txn) {
        Map<String, CoinbasePricing> paymentValues = payment.getValue();

        CoinbasePricing cryptoPricing = paymentValues.get("crypto");
        CoinbasePricing usdPricing = paymentValues.get("local");

        double usdAmount = Double.valueOf(usdPricing.getAmount());
        Bid bid = bidService.getBid(txn.getRoundId(), txn.getUserId());

        // wallet update confirmed amount make hold
        User user = userService.getUserById(txn.getUserId());

        if (bid.isPendingIncrease()) {
            double pendingPrice = bid.getDelta();
            if (pendingPrice > usdAmount) {
                return false;
            }

            bidService.updateBid(txn.getUserId(), txn.getRoundId(), bid.getTempTokenAmount(),
                    bid.getTempTokenPrice());

        } else {
            long totalPrice = bid.getTotalPrice();
            if (totalPrice > usdAmount) {
                return false;
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

        String cryptoType = cryptoPricing.getCurrency();
        String cryptoAmount = cryptoPricing.getAmount();
        cryptoService.updateTransaction(txn.getCode(), CryptoTransaction.CONFIRMED, cryptoAmount, cryptoType);
        
        // Change crypto Hold amount!!!!!!!!!!!!!!!
        // userWalletService.addHoldAmount(txn.getUserId(), cryptoType, cryptoAmount);

        // send notification to user for payment result!!
        notificationService.sendNotification(
                user.getId(),
                Notification.PAYMENT_RESULT,
                "PAYMENT CONFIRMED",
                "You have successfully deposited " + cryptoAmount + cryptoType + ".");

        bidService.updateBidRanking(txn.getUserId(), txn.getRoundId());
        return true;
    }

    private boolean handleDepositPayment(CoinbasePayments payment, DepositTransaction txn) {
        
        int userId = txn.getUserId();

        // 1) update transaction 
        depositTxnDao.updateStatus(txn.getCode());

        // 2) update user's internal balance
        CoinbasePricing cryptoPricing = payment.getValue().get("crypto");

        String cryptoType = cryptoPricing.getCurrency();
        double cryptoAmount = Double.valueOf(cryptoPricing.getAmount());

        balanceService.addFreeBalance(userId, cryptoType, cryptoAmount);

        // 3) check user's tier information and level if possible
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
        
        return true;
    }

}
