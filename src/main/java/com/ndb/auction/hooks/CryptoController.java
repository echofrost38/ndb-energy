package com.ndb.auction.hooks;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.User;
import com.ndb.auction.models.Wallet;
import com.ndb.auction.models.coinbase.CoinbaseEvent;
import com.ndb.auction.models.coinbase.CoinbaseEventBody;
import com.ndb.auction.models.coinbase.CoinbaseEventData;
import com.ndb.auction.models.coinbase.CoinbasePayments;
import com.ndb.auction.models.coinbase.CoinbasePricing;

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
    
    private static final String SHARED_SECRET = "a2282529-0865-4dbf-b837-d6f31db0e057";
	
    @PostMapping("/coinbase")
    @ResponseBody
    public ResponseEntity<?> CoinbaseWebhooks(HttpServletRequest request) {
        
        String hmac = request.getHeader("X-CC-Webhook-Signature");

		String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		String _hmac = buildHmacSignature(reqQuery, SHARED_SECRET);
    
        if(!hmac.equals(_hmac)) {
            return null;
        }
        CoinbaseEvent event = new Gson().fromJson(reqQuery, CoinbaseEvent.class);
        CoinbaseEventBody body = event.getEvent();
        
        if(body.getType().equals("charge.confirmed")) {
            CoinbaseEventData data = body.getData();
            CoinbasePayments payments = data.getPayments().get(0);
            
            if(payments.getStatus().equals("CONFIRMED")) {
                String code = data.getCode();
                CryptoTransaction txn = cryptoService.getTransactionById(code);
                
                Map<String, CoinbasePricing> paymentValues = payments.getValue();
                
                CoinbasePricing cryptoPricing = paymentValues.get("crypto");
                CoinbasePricing usdPricing = paymentValues.get("local");

                double usdAmount = Double.valueOf(usdPricing.getAmount());
                Bid bid = bidService.getBid(txn.getRoundId(), txn.getUserId());

                if(bid.getPendingIncrease()) {
                    double pendingPrice = bid.getDelta();
                    if(pendingPrice > usdAmount) {
                        new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                    
                    bidService.updateBid(txn.getUserId(), txn.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    
                } else {
                    double totalPrice = bid.getTotalPrice();
                    if(totalPrice > usdAmount) {
                        new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                }
                
                String cryptoType = cryptoPricing.getCurrency();
                Double cryptoAmount = Double.valueOf(cryptoPricing.getAmount());

                txn.setCryptoAmount(cryptoAmount);
                txn.setCryptoType(cryptoType);
                txn.setStatus(CryptoTransaction.CONFIRMED);
                cryptoService.updateTransaction(txn);

                // wallet update confirmed amount make hold
                User user = userService.getUserById(txn.getUserId());

                // update wallet !!
                // make simpler!
                Map<String, Wallet> tempWallet = user.getWallet();
                Wallet wallet = tempWallet.get(cryptoType);
                double balance = wallet.getHolding();
                wallet.setHolding(balance + cryptoAmount);
                tempWallet.replace(cryptoType, wallet);
                user.setWallet(tempWallet);

                userService.updateUser(user);

                // send notification to user for payment result!!

                bidService.updateBidRanking(txn.getUserId(), txn.getRoundId());
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
