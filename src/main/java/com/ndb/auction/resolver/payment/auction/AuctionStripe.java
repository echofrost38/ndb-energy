package com.ndb.auction.resolver.payment.auction;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeAuctionService;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuctionStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @Autowired
    private StripeAuctionService stripeAuctionService;

    @Autowired
	protected StripeCustomerService stripeCustomerService;
    
    // for stripe payment
    @PreAuthorize("isAuthenticated()")
    @CrossOrigin
    public String getStripePubKey() {
        return stripeAuctionService.getPublicKey();
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<StripeAuctionTransaction> getStripeAuctionTxByRound(int roundId) {
        return (List<StripeAuctionTransaction>) stripeAuctionService.selectByRound(roundId, null);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<StripeAuctionTransaction> getStripeAuctionTxByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(id, null);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<StripeAuctionTransaction> getStripeAuctionTxByAdmin(int userId) {
        return (List<StripeAuctionTransaction>) stripeAuctionService.selectByUser(userId, null);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeAuctionTransaction> getStripeAuctionTxForRoundByAdmin(int roundId, int userId) {
        return stripeAuctionService.selectByIds(roundId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripeAuctionTransaction> getStripeAuctionTx(int roundId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return stripeAuctionService.selectByIds(roundId, id);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForAuction(int roundId, Double amount, Double fiatAmount, String fiatType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var bid = bidService.getBid(roundId, userId);
        if(bid == null) {
            String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "roundId");
        }
        var usdAmount = bid.getTokenAmount() * bid.getTokenPrice();
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatamount = usdAmount * fiatPrice;
        StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, usdAmount, _fiatamount, fiatType, paymentIntentId, paymentMethodId);
        return stripeAuctionService.createNewTransaction(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForAuctionWithSavedCard(int roundId, Double amount, Double fiatAmount, String fiatType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if (userId != customer.getUserId()) {
            String msg = messageSource.getMessage("failed_auth_card", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "USER_ID");
        }
        var bid = bidService.getBid(roundId, userId);
        if(bid == null) {
            String msg = messageSource.getMessage("no_bid", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "roundId");
        }
        var usdAmount = bid.getTokenAmount() * bid.getTokenPrice();
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatamount = usdAmount * fiatPrice;
        StripeAuctionTransaction m = new StripeAuctionTransaction(userId, roundId, usdAmount, _fiatamount, fiatType, paymentIntentId);
        return stripeAuctionService.createNewTransactionWithSavedCard(m, customer);
    }
}
