package com.ndb.auction.service.payment.paypal;

import java.text.DecimalFormat;
import java.util.List;

import com.ndb.auction.models.transactions.paypal.PaypalTransaction;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.request.paypal.PurchaseUnit;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderStatus;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.utils.PaypalHttpClient;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaypalPresaleService extends PaypalBaseService {

    private final PaypalHttpClient payPalHttpClient;

    // Create new Paypal presale order
    public OrderResponseDTO insert(PaypalTransaction m) throws Exception {
                // create paypal checkout order
        
        var orderDTO = new OrderDTO();
        var df = new DecimalFormat("#.00");
        var unit = new PurchaseUnit(df.format(m.getFiatAmount()), m.getFiatType());
		orderDTO.getPurchaseUnits().add(unit);
        
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl(WEBSITE_URL + "/");
		appContext.setBrandName("Auction Round");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        orderDTO.setApplicationContext(appContext);

        OrderResponseDTO orderResponse = payPalHttpClient.createOrder(orderDTO);
        if(orderResponse.getStatus() != OrderStatus.CREATED) {
            return null;
        }
        m.setPaypalOrderId(orderResponse.getId());
        m.setPaypalOrderStatus(orderResponse.getStatus().toString());
        paypalTransactionDao.insert(m);
        return orderResponse;
    }

    public List<PaypalTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String txnType, String orderBy) {
        return paypalTransactionDao.selectPage(status, showStatus, offset, limit, txnType, orderBy);
    }

    public List<PaypalTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        return paypalTransactionDao.selectByUser(userId, showStatus, orderBy);
    }

    public PaypalTransaction selectById(int id) {
        return paypalTransactionDao.selectById(id);
    }

    public int updateOrderStatus(int id, String status) {
        return paypalTransactionDao.updateOrderStatus(id, status);
    }
    
}
