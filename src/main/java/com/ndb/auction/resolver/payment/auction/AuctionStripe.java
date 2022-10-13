package com.ndb.auction.resolver.payment.auction;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeAuctionService;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    private final StripeAuctionService stripeAuctionService;
	private final StripeCustomerService stripeCustomerService;
    
    // for stripe payment
    @PreAuthorize("isAuthenticated()")
    @CrossOrigin
    public String getStripePubKey() {
        return stripeAuctionService.getPublicKey();
    }

    // @PreAuthorize("hasRole('ROLE_SUPER')")
    // public List<StripeAuctionTransaction> getStripeAuctionTxByRound(int roundId) {
    //     return (List<StripeAuctionTransaction>) stripeAuctionService.selectByRound(roundId, null);
    // }

    @PreAuthorize("isAuthenticated()")
    public List<StripeTransaction> getStripeAuctionTxByUser(int showStatus, String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return stripeAuctionService.selectByUser(userId, showStatus, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeTransaction> getStripeAuctionTxByAdmin(int userId, String orderBy) {
        return stripeAuctionService.selectByUser(userId, 1, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeTransaction> getStripeAuctionTxForRoundByAdmin(int roundId, int userId) {
        return stripeAuctionService.selectByIds(roundId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripeTransaction> getStripeAuctionTx(int roundId) {
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

        double usdAmount = 0.0;
        if(bid.isPendingIncrease()) {
            usdAmount = bid.getTempTokenAmount() * bid.getTempTokenPrice() - bid.getTokenAmount() * bid.getTokenPrice();
        } else {
            usdAmount = bid.getTokenAmount() * bid.getTokenPrice();
        }
        var totalAmount = stripeAuctionService.getTotalAmount(userId, usdAmount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatamount = totalAmount * fiatPrice;
        var m = StripeTransaction.builder()
            .userId(userId)
            .txnType("AUCTION")
            .txnId(roundId)
            .intentId(paymentIntentId)
            .methodId(paymentMethodId)
            .fiatType(fiatType)
            .fiatAmount(_fiatamount)
            .usdAmount(totalAmount)
            .fee(totalAmount - usdAmount)
            .cryptoType("NDB")
            .cryptoAmount(bid.getTokenAmount())
            .status(false)
            .paymentStatus("PENDING")
            .shown(true)
            .build();        
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
        
        double usdAmount = 0.0;
        if(bid.isPendingIncrease()) {
            usdAmount = bid.getTempTokenAmount() * bid.getTempTokenPrice() - bid.getTokenAmount() * bid.getTokenPrice();
        } else {
            usdAmount = bid.getTokenAmount() * bid.getTokenPrice();
        }
        var totalAmount = stripeAuctionService.getTotalAmount(userId, usdAmount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatamount = totalAmount * fiatPrice;
        var m = StripeTransaction.builder()
            .userId(userId)
            .txnType("AUCTION")
            .txnId(roundId)
            .intentId(paymentIntentId)
            .methodId(customer.getPaymentMethod())
            .fiatType(fiatType)
            .fiatAmount(_fiatamount)
            .usdAmount(totalAmount)
            .fee(totalAmount - usdAmount)
            .cryptoType("NDB")
            .cryptoAmount(bid.getTokenAmount())
            .status(false)
            .paymentStatus("PENDING")
            .shown(true)
            .build();

        return stripeAuctionService.createNewTransactionWithSavedCard(m, customer);
    }
    
}
