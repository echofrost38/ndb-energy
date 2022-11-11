package com.ndb.auction.resolver.payment.deposit;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserSuspendedException;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.stripe.StripeCustomerService;
import com.ndb.auction.service.payment.stripe.StripeDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class DepositStripe extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    private final StripeDepositService stripeDepositService;
	private final StripeCustomerService stripeCustomerService;

    @Autowired
    public DepositStripe(StripeDepositService stripeDepositService, StripeCustomerService stripeCustomerService) {
        this.stripeDepositService = stripeDepositService;
        this.stripeCustomerService = stripeCustomerService;
    }

    // Deposit with Stripe
    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDeposit(Double amount, String fiatType, String paymentIntentId, String paymentMethodId, boolean isSaveCard) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        if (userService.isUserSuspended(userId)) {
            String msg = messageSource.getMessage("user_suspended", null, Locale.ENGLISH);
            throw new UserSuspendedException(msg);
        }

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if (!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        // amount in USDT to make deposit
        var totalUsdAmount = stripeDepositService.getTotalOrder(userId, amount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = totalUsdAmount * fiatPrice;

        log.info("deposit usd: {}", amount);
        log.info("total Order usd amount: {}", totalUsdAmount);
        log.info("fiat price: {}", fiatPrice);
        log.info("final amount: {}", _fiatAmount);

        var m = StripeTransaction.builder()
            .userId(userId)
            .txnType("DEPOSIT")
            .intentId(paymentIntentId)
            .methodId(paymentMethodId)
            .fiatType(fiatType)
            .fiatAmount(_fiatAmount)
            .usdAmount(amount)
            .cryptoType("USDT")
            .cryptoAmount(0)
            .paymentStatus("")
            .status(false)
            .shown(true)
            .build();
        return stripeDepositService.createDeposit(m, isSaveCard);
    }

    @PreAuthorize("isAuthenticated()")
    public PayResponse stripeForDepositWithSavedCard(Double amount, String fiatType, int cardId, String paymentIntentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        if (userService.isUserSuspended(userId)) {
            String msg = messageSource.getMessage("user_suspended", null, Locale.ENGLISH);
            throw new UserSuspendedException(msg);
        }

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if (!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        StripeCustomer customer = stripeCustomerService.getSavedCard(cardId);
        if (userId != customer.getUserId()) {
            String msg = messageSource.getMessage("failed_auth_card", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg,"USER_ID");
        }

        var totalUsdAmount = stripeDepositService.getTotalOrder(userId, amount);
        var fiatPrice = thirdAPIUtils.getCurrencyRate(fiatType);
        var _fiatAmount = totalUsdAmount * fiatPrice;

        log.info("deposit usd: {}", amount);
        log.info("total Order usd amount: {}", totalUsdAmount);
        log.info("fiat price: {}", fiatPrice);
        log.info("final amount: {}", _fiatAmount);

        var m = StripeTransaction.builder()
            .userId(userId)
            .txnType("DEPOSIT")
            .intentId(paymentIntentId)
            .methodId("")
            .fiatType(fiatType)
            .fiatAmount(_fiatAmount)
            .usdAmount(amount)
            .cryptoType("USDT")
            .cryptoAmount(0)
            .paymentStatus("")
            .status(false)
            .shown(true)
            .build();
        return stripeDepositService.createDepositWithSavedCard(m, customer);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeTransaction> getStripeDepositTx(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return stripeDepositService.selectAll(status, showStatus, offset, limit, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public List<StripeTransaction> getStripeDepositTxByUser(String orderBy, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return stripeDepositService.selectByUser(userId, showStatus, orderBy);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<StripeTransaction> getStripeDepositTxByAdmin(int userId, String orderBy) {
        // admin will get all transactions by default
        return stripeDepositService.selectByUser(userId, 1, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public StripeTransaction getStripeDepositTxById(int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var tx = stripeDepositService.selectById(id);
        if(tx.getUserId() == userId) {
            return tx;
        }
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    public int changeStripeDepositShowStatus(int id, int showStatus) {
        return stripeDepositService.changeShowStatus(id, showStatus);
    }
}
