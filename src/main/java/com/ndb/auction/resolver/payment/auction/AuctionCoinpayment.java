package com.ndb.auction.resolver.payment.auction;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.expression.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionCoinpayment extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    // for Coinpayments
	@PreAuthorize("isAuthenticated()")
	public CoinpaymentAuctionTransaction createCryptoPaymentForAuction(int roundId, Double amount, String cryptoType, String network, String coin) throws ParseException, IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		double total = getTotalCoinpaymentOrder(userId, amount);
		CoinpaymentAuctionTransaction _m = new CoinpaymentAuctionTransaction(roundId, userId, amount, total - amount, cryptoType, network, coin);
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
	@SuppressWarnings("unchecked")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByAdmin(int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}
	
	@PreAuthorize("isAuthenticated()")
	@SuppressWarnings("unchecked")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByUser(userId, null);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@SuppressWarnings("unchecked")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxByRound(int roundId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.selectByAuctionId(roundId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@SuppressWarnings("unchecked")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTxPerRoundByAdmin(int roundId, int userId) {
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	@SuppressWarnings("unchecked")
	public List<CoinpaymentAuctionTransaction> getCryptoAuctionTx(int roundId) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
		return (List<CoinpaymentAuctionTransaction>) coinpaymentAuctionService.select(userId, roundId);
	}

	@PreAuthorize("isAuthenticated()")
	public double getCryptoPrice(String symbol) {
		return 	thirdAPIUtils.getCryptoPriceBySymbol(symbol);
	}
}
