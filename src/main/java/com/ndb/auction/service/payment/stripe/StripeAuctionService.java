package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.payment.ITransactionService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.stereotype.Service;

@Service
public class StripeAuctionService extends StripeBaseService implements ITransactionService, IStripeDepositService {

    @Override
    public PayResponse createNewTransaction(StripeDepositTransaction _m) {
        StripeAuctionTransaction m = (StripeAuctionTransaction)_m;
        PaymentIntent intent;
        PayResponse response = new PayResponse();
        try {
            if (m.getPaymentIntentId() == null) {
            	
            	// Create new PaymentIntent for the order
                PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                        .setCurrency("usd")
                        .setAmount(m.getAmount().longValue())
                        .setPaymentMethod(m.getPaymentMethodId())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true)
                        .build();
                // Create a PaymentIntent with the order amount and currency
                intent = PaymentIntent.create(createParams);
                stripeAuctionDao.insert(m);
            } else {
                // Confirm the paymentIntent to collect the money
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
            }

            if(intent.getStatus().equals("requires_capture")) {
                stripeAuctionDao.update(m.getUserId(), m.getAuctionId(), intent.getId());
				Bid bid = bidService.getBid(m.getAuctionId(), m.getUserId());
                if(bid == null) {
                    throw new BidException("no_bid", "auctionId");
                }
                response.setError("Bid exists");
				double usdAmount = m.getAmount()/100.0;
				if(bid.isPendingIncrease()) {
                    double pendingPrice = bid.getDelta();
                    if(pendingPrice > usdAmount) {
                        response.setError("Insufficient funds");
						return response;
                    }
                    
                    bidService.increaseAmount(bid.getUserId(), bid.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    bid.setTokenAmount(bid.getTempTokenAmount());
                    bid.setTokenPrice(bid.getTempTokenPrice());
                } else {
                    long totalPrice = bid.getTokenAmount();
                    if(totalPrice > usdAmount) {
                        response.setError("Insufficient funds");
						return response;
                    }
                }
                response.setError("before ranking");
                bidService.updateBidRanking(bid);
                response.setError("After ranking");
            }
			response = generateResponse(intent, response);
                        
        } catch (Exception e) {
            // Handle "hard declines" e.g. insufficient funds, expired card, etc
            // See https://stripe.com/docs/declines/codes for more
        	// response.setError();
        }
		return response;
    }

    @Override
    public List<? extends StripeDepositTransaction> selectByIntentId(String intentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return stripeAuctionDao.selectAll(orderBy);
    }

    public List<StripeAuctionTransaction> selectByIds(int auctionId, int userId) {
        return stripeAuctionDao.selectByIds(auctionId, userId);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return stripeAuctionDao.selectByUser(userId, orderBy);
    }

    public List<? extends Transaction> selectByRound(int auctionId, String orderBy) {
        return stripeAuctionDao.selectByRound(auctionId, orderBy);
    }

    @Override
    public StripeAuctionTransaction selectById(int id) {
        return (StripeAuctionTransaction) stripeAuctionDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        return stripeAuctionDao.update(id, status);
    }

    public int update(int userId, int auctionId, String intentId) {
        return stripeAuctionDao.update(userId, auctionId, intentId);
    }

	// update payments - called by closeBid
	public boolean UpdateTransaction(int id, Integer status) {
		
		PaymentIntent intent;
		
		StripeAuctionTransaction tx = (StripeAuctionTransaction) stripeAuctionDao.selectById(id);
		if(tx == null) {
			return false;
		}
		
		String paymentIntentId = tx.getPaymentIntentId();
		try {
			intent = PaymentIntent.retrieve(paymentIntentId);
			if(status == Bid.WINNER) {
				intent.capture();		
				stripeAuctionDao.updatePaymentStatus(paymentIntentId, StripeDepositTransaction.CAPTURED);
			} else {
				intent.cancel();
				stripeAuctionDao.updatePaymentStatus(paymentIntentId, StripeDepositTransaction.CANCELED);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}	
		return true;
	}
}
