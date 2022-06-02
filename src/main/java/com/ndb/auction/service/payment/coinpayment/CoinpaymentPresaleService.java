package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.payload.request.coinpayments.CoinpaymentCreateTransaction;
import com.ndb.auction.payload.response.coinpayment.CreateTxResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoinpaymentPresaleService extends CoinpaymentBaseService {

    public CoinpaymentDepositTransaction createNewTransaction(String userEmail, CoinpaymentDepositTransaction m)
            throws UnsupportedEncodingException, ClientProtocolException, IOException {
        
        // round existing
        // check bid
        var presaleOrder = presaleOrderDao.selectById(m.getOrderId());
        if(presaleOrder == null || presaleOrder.getStatus() == 1) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        PreSale presale = presaleDao.selectById(presaleOrder.getPresaleId());
        if (presale == null) {
            String msg = messageSource.getMessage("no_presale", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        
        m = coinpaymentTransactionDao.insert(m);

        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/presale/" + m.getId();
        
        var txRequest = CoinpaymentCreateTransaction.builder()
            .amount(m.getAmount())
            .currency1("USD")
            .currency2(m.getCryptoType())
            .buyerEmail(userEmail)
            .ipnUrl(ipnUrl)
            .build();

        String payload = txRequest.toString();
        payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
        String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);
        
        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);
        
        String content = EntityUtils.toString(response.getEntity());
        log.info("Coinpayment Get Callback address response: {}", content);
        
        CreateTxResponse addressResponse = gson.fromJson(content, CreateTxResponse.class);
        if(!addressResponse.getError().equals("ok")) return null;
        String address = addressResponse.getResult().getAddress();
        
        double cryptoAmount = Double.valueOf(addressResponse.getResult().getAddress());
        double fee = getTierFee(m.getUserId(), cryptoAmount);
        coinpaymentTransactionDao.updateDepositAddress(m.getId(), cryptoAmount, fee, address);
        
        m.setDepositAddress(address);
        return m;
    }
        
}
