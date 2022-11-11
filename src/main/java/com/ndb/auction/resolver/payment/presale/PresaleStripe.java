package com.ndb.auction.resolver.payment.presale;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
// import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.payment.stripe.StripePresaleService;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresaleStripe extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private final StripePresaleService stripePresaleService;
	private final StripeCustomerService stripeCustomerService;

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForPreSale(int id, int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // getting value from presale order
        var order = presaleOrderService.getPresaleById(orderId);
        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        if(order.getStatus() != 0) {
            String msg = messageSource.getMessage("presale_processed", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        var usdAmount = order.getNdbAmount() * order.getNdbPrice();
        var totalUsdAmount = stripePresaleService.getTotalOrder(userId, usdAmount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = totalUsdAmount * fiatPrice;
        var m = StripeTransaction.builder()
            .id(id)
            .userId(userId)
            .txnType("PRESALE")
            .txnId(orderId)
            .intentId(paymentIntentId)
            .methodId(paymentMethodId)
            .fiatType(fiatType)
            .fiatAmount(_fiatAmount)
            .usdAmount(totalUsdAmount)
            .fee(totalUsdAmount - usdAmount)
            .cryptoType("NDB")
            .cryptoAmount(order.getNdbAmount())
            .status(false)
            .paymentStatus("Pending")
            .shown(true)
            .build();
        return stripePresaleService.createNewTransaction(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse payStripeForPreSaleWithSavedCard(int id, int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if(userId != customer.getUserId()){
            String msg = messageSource.getMessage("failed_auth_card", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg,"USER_ID");
        }

        // getting value from presale order
        var order = presaleOrderService.getPresaleById(orderId);
        
        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        if(order.getStatus() != 0) {
            String msg = messageSource.getMessage("presale_processed", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        var usdAmount = order.getNdbAmount() * order.getNdbPrice();
        var totalUsdAmount = stripePresaleService.getTotalOrder(userId, usdAmount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = totalUsdAmount * fiatPrice;
        var m = StripeTransaction.builder()
            .id(id)
            .userId(userId)
            .txnType("PRESALE")
            .txnId(orderId)
            .intentId(paymentIntentId)
            .methodId(customer.getPaymentMethod())
            .fiatType(fiatType)
            .fiatAmount(_fiatAmount)
            .usdAmount(totalUsdAmount)
            .fee(totalUsdAmount - usdAmount)
            .cryptoType("NDB")
            .cryptoAmount(order.getNdbAmount())
            .status(false)
            .paymentStatus("Pending")
            .shown(true)
            .build();
        return stripePresaleService.createNewTransactionWithSavedCard(m, customer);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripeTransaction> getStripePresaleTx(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return stripePresaleService.selectAll(status, showStatus, offset, limit, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripeTransaction> getStripePresaleTxByUser(String orderBy, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return stripePresaleService.selectByUser(userId, showStatus, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeTransaction> getStripePresaleTxByAdmin(int userId, String orderBy) {
        // admin will get all transactions by default
        return stripePresaleService.selectByUser(userId, 1, orderBy);
    }

    @PreAuthorize("isAuthenticated()")  
    public StripeTransaction getStripePresaleTxById(int id) {
        return stripePresaleService.selectById(id);
    }

    // @PreAuthorize("isAuthenticated()")
    // public List<StripeTransaction> getStripePresaleTxByPresaleId(int userId, int presaleId, String orderBy) {
    //     return stripePresaleService.selectByPresale(userId, presaleId, orderBy);
    // }

    @PreAuthorize("isAuthenticated()")
    public int changeStripePresaleShowStatus(int id, int showStatus) {
        return stripePresaleService.changeShowStatus(id, showStatus);
    }
}
