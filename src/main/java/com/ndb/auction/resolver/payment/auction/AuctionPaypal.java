package com.ndb.auction.resolver.payment.auction;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.request.paypal.PurchaseUnit;
import com.ndb.auction.payload.response.paypal.CaptureOrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderStatus;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.utils.PaypalHttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionPaypal extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver{

    private final PaypalHttpClient payPalHttpClient;

	@Autowired
	public AuctionPaypal(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}
    
    @PreAuthorize("isAuthenticated()")
	public OrderResponseDTO paypalForAuction(int roundId, String currencyCode) throws Exception {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

		// check bid status
		Auction round = auctionService.getAuctionById(roundId);
		if(round == null) {
			String msg = messageSource.getMessage("no_auction", null, Locale.ENGLISH);
			throw new AuctionException(msg, "roundId");
		}
		if(round.getStatus() != Auction.STARTED) {
			String msg = messageSource.getMessage("not_started", null, Locale.ENGLISH);
			throw new AuctionException(msg, "roundId");
		}
		Bid bid = bidService.getBid(roundId, userId);
		if(bid == null) {
			String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
			throw new BidException(msg, "roundId");
		}
		
		Double checkoutAmount = 0.0; // total amount in usd
		double fiatAmount = 0.0; // total amount in target currency
		Double amount = 0.0;
		if(bid.isPendingIncrease()) {
			amount = bid.getDelta(); // usd
			checkoutAmount = getPayPalTotalOrder(userId, amount);
		} else {
			amount = bid.getTotalAmount(); // usd
			checkoutAmount = getPayPalTotalOrder(userId, amount); 
		}

		if(currencyCode.equals("USD")) {
			fiatAmount = checkoutAmount;
		} else {
			fiatAmount = thirdAPIUtils.currencyConvert("USD", currencyCode, checkoutAmount);
		}

		OrderDTO order = new OrderDTO();

		DecimalFormat df = new DecimalFormat("#.00");
		PurchaseUnit unit = new PurchaseUnit(df.format(fiatAmount), currencyCode);
		order.getPurchaseUnits().add(unit);
		
		var appContext = new PayPalAppContextDTO();
        
		appContext.setReturnUrl(WEBSITE_URL + "/");
		appContext.setBrandName("Auction Round");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        order.setApplicationContext(appContext);
        
		OrderResponseDTO orderResponse = payPalHttpClient.createOrder(order);

		// Create not confirmed transaction
        PaypalAuctionTransaction entity = new PaypalAuctionTransaction(userId, roundId, fiatAmount, currencyCode, amount, checkoutAmount - amount, null, null);

		// set order id and status
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        
		paypalAuctionService.insert(entity);

		// return Order response
		return orderResponse;
	}

	@Transactional
	@PreAuthorize("isAuthenticated()")
	public boolean captureOrderForAuction(String orderId) throws Exception {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		
		CaptureOrderResponseDTO responseDTO = payPalHttpClient.captureOrder(orderId);
		if(responseDTO.getStatus() != null && responseDTO.getStatus().equals("COMPLETED")) {
			// fetch transaction
			PaypalAuctionTransaction m = (PaypalAuctionTransaction) paypalAuctionService.selectByPaypalOrderId(orderId);
			if(m == null) {
                String msg = messageSource.getMessage("no_transaction", null, Locale.ENGLISH);
				throw new BidException(msg, "orderId");
			}
			if(m.getUserId() != userId) {
                String msg = messageSource.getMessage("no_match_user", null, Locale.ENGLISH);
				throw new UserNotFoundException(msg, "user");
			}

			// check Bid
			Bid bid = bidService.getBid(m.getAuctionId(), m.getUserId());
			if(bid == null) {
                String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
				throw new BidException(msg, "orderId");
			} 
			if(bid.getStatus() != Bid.NOT_CONFIRMED && !bid.isPendingIncrease()) {
                String msg = messageSource.getMessage("cannot_capture", null, Locale.ENGLISH);
				throw new BidException(msg, "orderId");
			}

			// update transaction status
			paypalAuctionService.updateOrderStatus(m.getId(), OrderStatus.COMPLETED.toString());

			// update bid 
			bid.setPayType(Bid.PAYPAL);
			bidService.updateBidRanking(bid);
			return true;
		} else return false;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
	public List<PaypalAuctionTransaction> getAllPaypalAuctionTxns(String orderBy) {
		return (List<PaypalAuctionTransaction>) paypalAuctionService.selectAll(orderBy);
	}

	@PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalAuctionTransaction> getPaypalAuctionTxnsByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<PaypalAuctionTransaction>) paypalAuctionService.selectByUser(userId, orderBy);
    }
	
	@PreAuthorize("isAuthenticated()")
    public PaypalAuctionTransaction getPaypalAuctionTxn(int id) {
        return (PaypalAuctionTransaction) paypalAuctionService.selectById(id);
    }

}
