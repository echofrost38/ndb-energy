package com.ndb.auction.hooks;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.dao.FinancialDao;
import com.ndb.auction.models.FinancialTransaction;
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

@RestController
@RequestMapping("/")
public class FinancialController extends BaseController {
    @PostMapping("/deposit")
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

            if(data.getPayments().size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            CoinbasePayments payments = data.getPayments().get(0);
            
            if(payments.getStatus().equals("CONFIRMED")) {
                String code = data.getCode();

                List<FinancialTransaction> txs = financialDao.getTransactionByCode(code);
                if(txs.size() == 0) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);            
                }
                
                Map<String, CoinbasePricing> paymentValues = payments.getValue();
                CoinbasePricing cryptoPricing = paymentValues.get("crypto");
                CoinbasePricing usdPricing = paymentValues.get("local");

                double usdAmount = Double.valueOf(usdPricing.getAmount());
                
                FinancialTransaction tx = txs.get(0);
                
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
