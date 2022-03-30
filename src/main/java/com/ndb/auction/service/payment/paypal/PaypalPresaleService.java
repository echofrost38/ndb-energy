package com.ndb.auction.service.payment.paypal;

import java.text.DecimalFormat;
import java.util.List;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.request.paypal.PurchaseUnit;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderStatus;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.service.payment.ITransactionService;
import com.ndb.auction.utils.PaypalHttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaypalPresaleService extends PaypalBaseService implements ITransactionService {

    private final PaypalHttpClient payPalHttpClient;

    @Autowired
	public PaypalPresaleService(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}

    // Create new Paypal presale order
    public OrderResponseDTO insert(PaypalPresaleTransaction m) throws Exception {
        // get presale 
        int presaleId = m.getPresaleId();
        PreSale presale = presaleDao.selectById(presaleId);
        if(presale == null || presale.getStatus() != PreSale.STARTED) {
            throw new AuctionException("There is a problem in Presale Round.", "presaleId");
        }

        // create paypal checkout order
        double orderAmount = (double)(m.getAmount());
        double totalOrder = getPayPalTotalOrder(m.getUserId(), orderAmount);

        var orderDTO = new OrderDTO();
        var df = new DecimalFormat("#.00");
        var unit = new PurchaseUnit(df.format(totalOrder), m.getFiatType());
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
        paypalPresaleDao.insert(m);
        return orderResponse;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return paypalPresaleDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return paypalPresaleDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return paypalPresaleDao.selectById(id);
    }

    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        return paypalPresaleDao.selectByPaypalOrderId(orderId);
    }

    @Override
    public int update(int id, int status) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int updateOrderStatus(int id, String status) {
        return paypalPresaleDao.updateOrderStatus(id, status);
    }
    
}
