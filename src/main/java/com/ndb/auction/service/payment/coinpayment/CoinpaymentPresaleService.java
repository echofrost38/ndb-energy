package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.ndb.auction.exceptions.PaymentException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.nyyupay.NyyuPayPendingRequest;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;
import com.ndb.auction.payload.response.PendingRequestResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoinpaymentPresaleService extends CoinpaymentBaseService {

    @Value("${nyyupay.pubKey}")
    public String NYYU_PUB_KEY;

    @Value("${nyyupay.privKey}")
    public String NYYU_PRIV_KEY;

    @Value("${nyyupay.base}")
    public String NYYU_BASE_URL;

    @Value("${api.base}")
    public String API_BASE;

    public CoinpaymentDepositTransaction createNewTransaction(CoinpaymentDepositTransaction m)
            throws UnsupportedEncodingException, ClientProtocolException, IOException {
        
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
        HttpPost post;
        m = coinpaymentTransactionDao.insert(m);
        var walletAddress = "";
        switch (m.getNetwork()){
            case "BEP20" :
                // send pending request
                post = new HttpPost(NYYU_BASE_URL + "/pending-requests");
                post.addHeader("Connection", "close");
                post.addHeader("Content-Type", "application/json; charset=utf-8");

                var nyyuWallet = nyyuWalletService.selectByUserId(m.getUserId());

                // check nyyuWallet is registered or not
                if (nyyuWallet != null){
                    if(nyyuWallet.getNyyuPayRegistered()) {
                        walletAddress = nyyuWallet.getPublicKey();
                    } else {
                        var address = nyyuWalletService.registerNyyuWallet(nyyuWallet);
                        if(address == null) {
                            String msg = messageSource.getMessage("no_registered_wallet", null, Locale.ENGLISH);
                            throw new PaymentException(msg, "cryptoType");
                        }
                        walletAddress = address;
                    }
                } else {
                    var address = nyyuWalletService.generateBEP20Address(m.getUserId());
                    if(address == null) {
                        String msg = messageSource.getMessage("no_registered_wallet", null, Locale.ENGLISH);
                        throw new PaymentException(msg, "cryptoType");
                    }
                    walletAddress = address;
                }

                long ts = System.currentTimeMillis() / 1000L;
                var nyyuPayPendingRequest = NyyuPayPendingRequest.builder()
                        .address(walletAddress)
                        .callback(API_BASE + "/nyyupay/presale/" + m.getId())
                        .network("BEP20")
                        .cryptoType(m.getCryptoType())
                        .build();

                String nyyuPayload = String.valueOf(ts) + "POST" + gson.toJson(nyyuPayPendingRequest);
                String nyyuHmac = buildHmacSignature(nyyuPayload, NYYU_PRIV_KEY);
                post.addHeader("x-auth-token", nyyuHmac);
                post.addHeader("x-auth-key", NYYU_PUB_KEY);
                post.addHeader("x-auth-ts", String.valueOf(ts));
                post.setEntity(new StringEntity(gson.toJson(nyyuPayPendingRequest)));
                
                CloseableHttpResponse nyyuPayResponse = client.execute(post);
                String nyyuPayContent = EntityUtils.toString(nyyuPayResponse.getEntity());
                log.info("Nyyupay pending-requests response: {}", nyyuPayContent);

                var res = gson.fromJson(nyyuPayContent, PendingRequestResponse.class);
                if(!res.getStatus().equals("PENDING")) return null;

                coinpaymentTransactionDao.updateDepositAddress(m.getId(), walletAddress);
                m.setDepositAddress(walletAddress);
                break;
            default:
                post = new HttpPost(COINS_API_URL);
                post.addHeader("Connection", "close");
                post.addHeader("Accept", "*/*");
                post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                post.addHeader("Cookie2", "$Version=1");
                post.addHeader("Accept-Language", "en-US");
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
                m.setDepositAddress(address);
                break;
        }
        return m;
    }
        
}
