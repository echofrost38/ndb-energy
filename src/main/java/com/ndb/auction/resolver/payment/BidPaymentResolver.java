package com.ndb.auction.resolver.payment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.payload.response.paypal.OrderDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.payload.response.paypal.PurchaseUnit;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.utils.PaypalHttpClient;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BidPaymentResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
	
	private final double PAYPAL_FEE = 5;

	private final PaypalHttpClient payPalHttpClient;

	@Autowired
	public BidPaymentResolver(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}

	// for stripe payment
	@PreAuthorize("isAuthenticated()")
	@CrossOrigin
	public String getStripePubKey() {
		return stripeBaseService.getPublicKey();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxByRound(int roundId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByRound(roundId, null);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeAuctionTxByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(id, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxByAdmin(int userId) {
		return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StripeAuctionTransaction> getStripeAuctionTxForRoundByAdmin(int roundId, int userId) {
		return stripeAuctionService.selectByIds(roundId, userId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<StripeAuctionTransaction> getStripeAuctionTx(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
		return stripeAuctionService.selectByIds(roundId, id);
	}

	@PreAuthorize("isAuthenticated()")
	public PayResponse payStripeForAuction(
		int roundId, 
		Long amount,
		String paymentIntentId,
		String paymentMethodId,
		boolean isSaveCard
	) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, amount, paymentIntentId, paymentMethodId, isSaveCard);
		return stripeAuctionService.createNewTransaction(m);
	}

	// for Coinpayments
	@PreAuthorize("isAuthenticated()")
	public CoinpaymentAuctionTransaction createCryptoPaymentForAuction(int roundId, Long amount, String cryptoType, String network, String coin) throws ParseException, IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		CoinpaymentAuctionTransaction _m = new CoinpaymentAuctionTransaction(roundId, userId, amount, cryptoType, network, coin);
		return (CoinpaymentAuctionTransaction) coinpaymentAuctionService.createNewTransaction(_m);
	}

	// @PreAuthorize("isAuthenticated()")
	public String getExchangeRate() throws ParseException, IOException {
		return coinpaymentAuctionService.getExchangeRate();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public CoinpaymentAuctionTransaction getCryptoAuctionTxById(int id) {
		return (CoinpaymentAuctionTransaction) coinpaymentAuctionService.selectById(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByAdmin(int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByRound(int roundId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByAuctionId(roundId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxPerRoundByAdmin(int roundId, int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTx(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public double getCryptoPrice(String symbol) {
		return 	thirdAPIUtils.getCryptoPriceBySymbol(symbol);
	}

	// for paypal payments!
	@PreAuthorize("isAuthenticated()")
	public OrderResponseDTO paypalForAuction(int roundId, Double amount, String currencyCode) throws Exception {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

		if(amount <= 0) throw new AuctionException("bad_request", "amount");
		
		// check bid status
		Auction round = auctionService.getAuctionById(roundId);
		if(round == null) {
			throw new AuctionException("no_auction", "roundId");
		}
		if(round.getStatus() != Auction.STARTED) {
			throw new AuctionException("not_started_auction", "roundId");
		}
		Bid bid = bidService.getBid(roundId, userId);
		if(bid == null) {
			throw new BidException("not_bid", "roundId");
		}
		
		Double checkoutAmount = 0.0;
		if(bid.isPendingIncrease()) {
			double pendingPrice = bid.getDelta();
			checkoutAmount = getPayPalTotalOrder(userId, pendingPrice);
		} else {
			Double totalPrice = bid.getTotalAmount();
			checkoutAmount = getPayPalTotalOrder(userId, totalPrice);
		}


		OrderDTO order = new OrderDTO();

		DecimalFormat df = new DecimalFormat("#.00");
		PurchaseUnit unit = new PurchaseUnit(df.format(checkoutAmount), currencyCode);
		order.getPurchaseUnits().add(unit);
		
		var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl(PAYPAL_CALLBACK_URL + "/auction");
        appContext.setBrandName("Auction Round");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        order.setApplicationContext(appContext);
        
		OrderResponseDTO orderResponse = payPalHttpClient.createOrder(order);

		// Create not confirmed transaction
        PaypalAuctionTransaction entity = new PaypalAuctionTransaction(userId, roundId, amount.longValue(), null, null);

		// set order id and status
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        
		paypalAuctionService.insert(entity);

		// return Order response
		return orderResponse;
	}

	private double getPayPalTotalOrder(int userId, double amount) {
		User user = userService.getUserById(userId);
		Double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		return 100 * (amount + 0.30) / (100 - PAYPAL_FEE - tierFeeRate);
	}

	// NDB Wallet
	@PreAuthorize("isAuthenticated()")
	public String payWalletForAuction(int roundId, String cryptoType) {
		Auction auction = auctionService.getAuctionById(roundId);
		if(auction == null) {
			throw new AuctionException("no_auction", "roundId");
		}
		if(auction.getStatus() != Auction.STARTED) {
			throw new AuctionException("not_started", "roundId");
		}

		// Get Bid
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		
		Bid bid = bidService.getBid(roundId, userId);
		User user = userService.getUserById(userId);
		if(bid == null) throw new BidException("no_bid", "roundId");

		// Get total order in USD
		double totalOrder = 0.0;
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
		if(bid.isPendingIncrease()) {
			double delta = bid.getDelta();
			totalOrder = 100 * delta / (100 - tierFeeRate);
		} else {
			double totalPrice = (double) (bid.getTokenPrice() * bid.getTokenAmount());
			totalOrder = 100 * totalPrice / (100 - tierFeeRate);
		}

		// check crypto Type balance
		double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
		double cryptoAmount = totalOrder / cryptoPrice; // required amount!
		double freeBalance = internalBalanceService.getFreeBalance(userId, cryptoType);
		if(freeBalance < cryptoAmount) throw new BidException("insufficient", "amount");

		// make hold
		internalBalanceService.makeHoldBalance(userId, cryptoType, cryptoAmount);
		
		// update holding list
		Map<String, BidHolding> holdingList = bid.getHoldingList();
		BidHolding hold = null;
		if(holdingList.containsKey(cryptoType)) {
			hold = holdingList.get(cryptoType);
			double currentAmount = hold.getCrypto();
			hold.setCrypto(currentAmount + cryptoAmount);
		} else {
			hold = new BidHolding(cryptoAmount, totalOrder);
			holdingList.put(cryptoType, hold);
		}

		// update bid
		bidService.updateHolding(bid);
		long newAmount = bid.getTempTokenAmount();
		long newPrice = bid.getTempTokenPrice();
		bidService.increaseAmount(userId, roundId, newAmount, newPrice);

		// update bid Ranking
		bid.setTokenAmount(newAmount);
		bid.setTokenPrice(newPrice);
		bidService.updateBidRanking(bid);
		return "SUCCESS";
	}

}
