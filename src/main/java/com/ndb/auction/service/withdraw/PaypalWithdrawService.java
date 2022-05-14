package com.ndb.auction.service.withdraw;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.payload.request.paypal.Item;
import com.ndb.auction.payload.request.paypal.PayoutsDTO;
import com.ndb.auction.payload.request.paypal.SenderBatchHeader;
import com.ndb.auction.payload.response.paypal.BatchHeader.Amount;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.PaypalHttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaypalWithdrawService extends BaseService implements IWithdrawService {

    private final PaypalHttpClient payPalHttpClient;
    private ObjectMapper mapper = new ObjectMapper();

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
        var m = (PaypalWithdraw)paypalWithdrawDao.selectById(requestId);
        if(status == BaseWithdraw.APPROVE) {
            // approve withdraw money
            
            if(m == null) {
                String msg = messageSource.getMessage("no_withdrawal_request", null, Locale.ENGLISH);
			    throw new UserNotFoundException(msg, "withdrawal request");
            }

            var tokenId = tokenAssetService.getTokenIdBySymbol(m.getSourceToken());
            var balance = balanceDao.selectById(m.getUserId(), tokenId);
            if(balance.getFree() < m.getTokenAmount()) {
                String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
                throw new BalanceException(msg, "amount");
            }

            // create payouts request body
            var batchId = generateBatchId(m);
            var batchHeader = new SenderBatchHeader(batchId);
            var itemId = generateItemId(m);

            var df = new DecimalFormat("#.00");
            var amount = new Amount(m.getTargetCurrency(), df.format(m.getWithdrawAmount()));
            var item = new Item(amount, itemId, m.getReceiver());   
            var payoutsDTO = new PayoutsDTO(batchHeader, item);

            // sending payout to PayPal
            var response = payPalHttpClient.createPayout(payoutsDTO);
            var batchHeaderResponse = response.getBatch_header();
            // check status!
            if(batchHeaderResponse == null || batchHeaderResponse.getBatch_status().equals("DENIED")) {
                log.info("Batch Header Response: {}", mapper.writeValueAsString(batchHeaderResponse));
                // send failed notification
                notificationService.sendNotification(
                    m.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    String.format("Your PayPal withdarwal request has been failed. %s", reason));
                return paypalWithdrawDao.confirmWithdrawRequest(requestId, BaseWithdraw.DENIED, "Cannot create payout");
            }

            paypalWithdrawDao.updatePaypalID(m.getId(), batchHeaderResponse.getPayout_batch_id(), batchId, itemId);
        } else {
            notificationService.sendNotification(
                    m.getUserId(),
                    Notification.PAYMENT_RESULT,
                    "PAYMENT CONFIRMED",
                    String.format("Your PayPal withdarwal request has been denied. %s", reason));
        }
        // submitted status!!!!!
        return paypalWithdrawDao.confirmWithdrawRequest(requestId, status, reason);
    }

    public int updateWithdrawRequest(int requestId, int status, String reason) {
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

    public List<? extends BaseWithdraw> getAllWithdrawRequests() {
        return paypalWithdrawDao.selectAll();
    }

    @Override
    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests() {
        return paypalWithdrawDao.selectPendings();
    }

    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests(int userId) {
        return paypalWithdrawDao.selectPendings(userId);
    }

    @Override
    public BaseWithdraw getWithdrawRequestById(int id) {
        return paypalWithdrawDao.selectById(id);
    }

    public BaseWithdraw getWithdrawRequestById(int id, int userId) {
        return paypalWithdrawDao.selectById(id, userId);
    }

    public PaypalWithdraw getWithdrawByPayoutId(String payoutId) {
        return paypalWithdrawDao.selectByPayoutId(payoutId);
    }

    /// paypal withdraw utils
    private String generateBatchId(PaypalWithdraw m) {
        return String.format("ndb-withdraw-sender-%d", m.getId());
    }

    private String generateItemId(PaypalWithdraw m) {
        return String.format("ndb-withdraw-item-%d", m.getId());
    }
    
}
