package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.payload.request.paypal.Item;
import com.ndb.auction.payload.request.paypal.PayoutsDTO;
import com.ndb.auction.payload.request.paypal.SenderBatchHeader;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.PaypalHttpClient;
import com.paypal.api.payments.Amount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaypalWithdrawService extends BaseService implements IWithdrawService {

    private final PaypalHttpClient payPalHttpClient;

	@Autowired
	public PaypalWithdrawService(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}

    @Override
    public BaseWithdraw createNewWithdrawRequest(BaseWithdraw baseWithdraw) {
        var m = (PaypalWithdraw)baseWithdraw;
        return paypalWithdrawDao.insert(m);
    }

    @Override
    public int confirmWithdrawRequest(int requestId, int status, String reason) throws Exception {
        
        // check status 
        if(status == BaseWithdraw.APPROVE) {
            // approve withdraw money
            var m = (PaypalWithdraw)paypalWithdrawDao.selectById(requestId);
            
            if(m == null) {
                throw new UserNotFoundException("There is no such withdraw request", "requestId");
            }

            // create payouts request body
            var batchId = generateBatchId(m);
            var batchHeader = new SenderBatchHeader(batchId);
            var itemId = generateItemId(m);
            var amount = new Amount(String.valueOf(m.getWithdrawAmount()), "USD");
            var item = new Item(amount, itemId, m.getReceiver());   
            var payoutsDTO = new PayoutsDTO(batchHeader, item);

            // sending payout to PayPal
            var batchHeaderResponse = payPalHttpClient.createPayout(payoutsDTO);
            
            // check status!
            if(batchHeaderResponse.getBatch_status().equals("DENIED")) {
                return paypalWithdrawDao.confirmWithdrawRequest(requestId, BaseWithdraw.DENIED, "Cannot create payout");
            }
            paypalWithdrawDao.updatePaypalID(m.getId(), batchId, itemId);
        }
        return paypalWithdrawDao.confirmWithdrawRequest(requestId, status, reason);
    }

    @Override
    public List<? extends BaseWithdraw> getWithdrawRequestByUser(int userId) {
        return paypalWithdrawDao.selectByUser(userId);
    }

    @Override
    public List<? extends BaseWithdraw> getWithdrawRequestByStatus(int userId, int status) {
        return paypalWithdrawDao.selectByStatus(userId, status);
    }

    @Override
    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests() {
        return paypalWithdrawDao.selectPendings();
    }

    @Override
    public BaseWithdraw getWithdrawRequestById(int id) {
        return paypalWithdrawDao.selectById(id);
    }

    /// paypal withdraw utils
    private String generateBatchId(PaypalWithdraw m) {
        return String.format("ndb-withdraw-sender-%d", m.getId());
    }

    private String generateItemId(PaypalWithdraw m) {
        return String.format("ndb-withdraw-item-%d", m.getId());
    }
    
}
