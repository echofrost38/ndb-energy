package com.ndb.auction.service;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.payload.CryptoPayload;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PresaleOrderService extends BaseService {
    
    public PresaleOrderService(WebClient.Builder webClientBuilder) {
        this.coinPaymentAPI = webClientBuilder
                .baseUrl(COINS_API_URL)
                .build();
    }
    
    // create new presale order
    public PreSaleOrder placePresaleOrder(PreSaleOrder order) {
        return presaleOrderDao.insert(order);
    }
    
    public CryptoPayload payOrderWithCrypto(int orderId) {

        // insert dataabase
        PreSaleOrder order = presaleOrderDao.selectById(orderId);
        if(order == null || order.getStatus() == 1) {
            throw new PreSaleException("no_presale_order", "orderId");
        }

        Long _amount = order.getNdbAmount() * order.getNdbPrice();
        String amount = _amount.toString();
        
        // API call for create new charge
        
        return null;
    }



    public PreSaleOrder getPresaleById(int orderId) {
        return presaleOrderDao.selectById(orderId);
    }

    public int updateStatus(int orderId) {
        return presaleOrderDao.updateStatus(orderId);
    }

}
