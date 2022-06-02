package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.presale.PreSaleOrder;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PresaleOrderService extends BaseService {
    
    protected CloseableHttpClient client;

    public PresaleOrderService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.coinPaymentAPI = webClientBuilder
                .baseUrl(COINS_API_URL)
                .build();
    }
    
    // create new presale order
    public PreSaleOrder placePresaleOrder(PreSaleOrder order) {
        return presaleOrderDao.insert(order);
    }

    public PreSaleOrder getPresaleById(int orderId) {
        return presaleOrderDao.selectById(orderId);
    }

    public int updateStatus(int orderId) {
        return presaleOrderDao.updateStatus(orderId);
    }

    public List<PreSaleOrder> getPresaleOrders(int presaleId) {
        return presaleOrderDao.selectByPresaleId(presaleId);
    }

    public List<PreSaleOrder> getPresaleOrdersByUserId(int userId) {
        return presaleOrderDao.selectAllByUserId(userId);
    }

    

}
