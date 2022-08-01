package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;

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

    public CoinpaymentDepositTransaction createNewTransaction(CoinpaymentDepositTransaction m)
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
        switch (m.getNetwork()){
            case "BEP20" :
                NyyuWallet nyyuWallet = nyyuWalletService.selectByUserId(m.getUserId());
                long ts = System.currentTimeMillis() / 1000L;
                String nyyuPayload = String.valueOf(ts) +"POST"+"{\"address\":\""+nyyuWallet.getPublicKey()+"\"}";
                String nyyuHmac = buildHmacSignature(nyyuPayload, NYYU_PRIV_KEY);
                post.addHeader("X-Auth-Token",  nyyuHmac);
                post.addHeader("X-Auth-Key",  NYYU_PRIV_KEY);
                post.addHeader("X-Auth-Ts",  String.valueOf(ts));
                post.setEntity(new StringEntity(nyyuPayload));
                CloseableHttpResponse nyyuPayResponse = client.execute(post);
                String nyyuPayContent = EntityUtils.toString(nyyuPayResponse.getEntity());
                log.info("Coinpayment Get Callback address response: {}", nyyuPayContent);
                coinpaymentTransactionDao.updateDepositAddress(m.getId(), nyyuWallet.getPublicKey());
            default:
                // get address
                String ipnUrl = COINSPAYMENT_IPN_URL + "/presale/" + m.getId();
                CoinPaymentsGetCallbackRequest request = new CoinPaymentsGetCallbackRequest(m.getCoin(), ipnUrl);

                String payload = request.toString();
                payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
                String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);

                post.addHeader("HMAC", hmac);
                post.setEntity(new StringEntity(payload));
                CloseableHttpResponse response = client.execute(post);

                String content = EntityUtils.toString(response.getEntity());
                log.info("Coinpayment Get Callback address response: {}", content);

                AddressResponse addressResponse = gson.fromJson(content, AddressResponse.class);
                if(!addressResponse.getError().equals("ok")) return null;
                String address = addressResponse.getResult().getAddress();
                coinpaymentTransactionDao.updateDepositAddress(m.getId(), address);
        }

        return coinpaymentTransactionDao.selectById(m.getId());
    }
        
}
