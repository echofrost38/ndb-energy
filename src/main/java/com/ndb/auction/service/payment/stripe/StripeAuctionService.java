package com.ndb.auction.service.payment.stripe;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeTransactionDao;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeAuctionService extends StripeBaseService {

    private final StripeTransactionDao stripeTransactionDao;

    public PayResponse createNewTransaction(StripeTransaction m, boolean isSaveCard) {
        PaymentIntent intent;
        PayResponse response = new PayResponse();
        double totalAmount = getTotalAmount(m.getUserId(),m.getFiatAmount());
        m.setFee(getStripeFee(m.getUserId(), m.getFiatAmount()));
        totalAmount *= 100; // convert into cent
        try {
            if (m.getIntentId() == null) {

                // Create new PaymentIntent for the order
                PaymentIntentCreateParams.Builder createParams = new PaymentIntentCreateParams.Builder()
                        .setCurrency(m.getFiatType())
                        .setAmount((long) totalAmount)
                        .setPaymentMethod(m.getMethodId())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true);

                // check save card
                if (isSaveCard) {
                    createParams = saveStripeCustomer(createParams, m);
                }

                // Create a PaymentIntent with the order amount and currency
                intent = PaymentIntent.create(createParams.build());
                stripeTransactionDao.insert(m);
            } else {
                // Confirm the paymentIntent to collect the money
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.updatePaymentIntent(m.getId(), m.getIntentId());
            }

            if (intent.getStatus().equals("requires_capture")) {
                stripeTransactionDao.update(m.getUserId(), m.getTxnId(), "AUCTION", intent.getId());
                Bid bid = bidService.getBid(m.getTxnId(), m.getUserId());
                if (bid == null) {
                    String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
                    throw new BidException(msg, "bid");
                }

                // double paidAmount = intent.getAmount().doubleValue();

                if (bid.isPendingIncrease()) {
                    // double pendingPrice = bid.getDelta();
                    // Double totalOrder = getTotalOrder(bid.getUserId(), pendingPrice);
                    // if(totalOrder * 100 > paidAmount) {
                    //     response.setError("Insufficient funds");
                    // 	return response;
                    // }

                    bidService.increaseAmount(bid.getUserId(), bid.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    bid.setTokenAmount(bid.getTempTokenAmount());
                    bid.setTokenPrice(bid.getTempTokenPrice());
                } else {
                    // Long totalPrice = bid.getTokenAmount();
                    // Double totalOrder = getTotalOrder(bid.getUserId(), totalPrice.doubleValue());
                    // if(totalOrder * 100 > paidAmount) {
                    //     response.setError("Insufficient funds");
                    // 	return response;
                    // }
                }
                bid.setPayType(Bid.STRIPE);
                bidService.updateBidRanking(bid);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            // Handle "hard declines" e.g. insufficient funds, expired card, etc
            // See https://stripe.com/docs/declines/codes for more
            response.setError(e.getMessage());
        }
        return response;
    }

    public PayResponse createNewTransactionWithSavedCard(StripeTransaction m, StripeCustomer customer) {
        PaymentIntent intent;
        PayResponse response = new PayResponse();
        double totalAmount = getTotalAmount(m.getUserId(),m.getFiatAmount());
        m.setFee(getStripeFee(m.getUserId(), m.getFiatAmount()));
        totalAmount *= 100;
        try {

            if(m.getIntentId() == null) {
                // Create new PaymentIntent for the order
                PaymentIntentCreateParams.Builder createParams = new PaymentIntentCreateParams.Builder()
                        .setCurrency(m.getFiatType())
                        .setAmount((long) totalAmount)
                        .setCustomer(customer.getCustomerId())
                        .setPaymentMethod(customer.getPaymentMethod())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true);

                // Create a PaymentIntent with the order amount and currency
                intent = PaymentIntent.create(createParams.build());
                stripeTransactionDao.insert(m);
            } else {
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.insert(m);
            }

            if (intent.getStatus().equals("requires_capture")) {
                stripeTransactionDao.update(m.getUserId(), m.getTxnId(), "AUCTION", intent.getId());
                Bid bid = bidService.getBid(m.getTxnId(), m.getUserId());
                if (bid == null) {
                    String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
                    throw new BidException(msg, "bid");
                }

                // double paidAmount = intent.getAmount().doubleValue();

                if (bid.isPendingIncrease()) {
                    // double pendingPrice = bid.getDelta();
                    // Double totalOrder = getTotalOrder(bid.getUserId(), pendingPrice);
                    // if(totalOrder * 100 > paidAmount) {
                    //     response.setError("Insufficient funds");
                    // 	return response;
                    // }

                    bidService.increaseAmount(bid.getUserId(), bid.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    bid.setTokenAmount(bid.getTempTokenAmount());
                    bid.setTokenPrice(bid.getTempTokenPrice());
                } else {
                    // Long totalPrice = bid.getTokenAmount();
                    // Double totalOrder = getTotalOrder(bid.getUserId(), totalPrice.doubleValue());
                    // if(totalOrder * 100 > bid.getPaidAmount()) {
                    //     response.setError("Insufficient funds");
                    // 	return response;
                    // }
                }
                bid.setPayType(Bid.STRIPE);
                bidService.updateBidRanking(bid);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            // Handle "hard declines" e.g. insufficient funds, expired card, etc
            // See https://stripe.com/docs/declines/codes for more
            response.setError(e.getMessage());
        }
        return response;
    }

    public List<StripeTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return stripeTransactionDao.selectPage(status, showStatus, offset, limit, "AUCTION", orderBy);
    }

    public List<StripeTransaction> selectByIds(int auctionId, int userId) {
        return stripeTransactionDao.selectByIds(userId, auctionId, "AUCTION");
    }

    public List<StripeTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        return stripeTransactionDao.selectByUser(userId, showStatus, orderBy);
    }

    // public List<StripeTransaction> selectByRound(int auctionId, String orderBy) {
    //     return stripeTransactionDao.selectByRound(auctionId, orderBy);
    // }

    public StripeTransaction selectById(int id) {
        return stripeTransactionDao.selectById(id);
    }

    public int update(int id, int status) {
        return stripeTransactionDao.update(id, status);
    }

    public int update(int userId, int auctionId, String intentId) {
        return stripeTransactionDao.update(userId, auctionId, "AUCTION", intentId);
    }

    // update payments - called by closeBid
    public boolean UpdateTransaction(int id, Integer status) {

        PaymentIntent intent;
        StripeTransaction tx = stripeTransactionDao.selectById(id);
        if (tx == null) {
            return false;
        }

        String paymentIntentId = tx.getIntentId();
        try {
            intent = PaymentIntent.retrieve(paymentIntentId);
            if (status == Bid.WINNER) {
                intent.capture();
                stripeTransactionDao.updatePaymentStatus(paymentIntentId, StripeDepositTransaction.CAPTURED);
            } else {
                intent.cancel();
                stripeTransactionDao.updatePaymentStatus(paymentIntentId, StripeDepositTransaction.CANCELED);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
