package com.ndb.auction.resolver.payment.presale;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.paypal.PaypalTransaction;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.request.paypal.PurchaseUnit;
import com.ndb.auction.payload.response.paypal.CaptureOrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderStatus;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.paypal.PaypalPresaleService;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.utils.PaypalHttpClient;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresalePaypal extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    private final PaypalPresaleService paypalPresaleService;
    private final PaypalHttpClient payPalHttpClient;

    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO paypalForPresale(int presaleId, int orderId, String currencyCode) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        
        var presale = presaleService.getPresaleById(presaleId);
        if(presale == null || presale.getStatus() != PreSale.STARTED) {
            String msg = messageSource.getMessage("already_in_action", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(orderId);
        if(presaleOrder == null) {
            String msg = messageSource.getMessage("no_presale", null, Locale.ENGLISH);
            throw new BidException(msg, "orderId");
        }

        if(presaleOrder.getStatus() != 0) {
            String msg = messageSource.getMessage("presale_processed", null, Locale.ENGLISH);
            throw new BidException(msg, "order");
        }

        double amount = presaleOrder.getNdbAmount() * presaleOrder.getNdbPrice();
        
        var checkoutAmount = paypalPresaleService.getPayPalTotalOrder(userId, amount);
        var fiatAmount = 0.0;
        if(currencyCode.equals("USD")) {
			fiatAmount = checkoutAmount;
		} else {
			fiatAmount = thirdAPIUtils.currencyConvert("USD", currencyCode, checkoutAmount);
		}

        var order = new OrderDTO();
        var df = new DecimalFormat("#.00");
        var unit = new PurchaseUnit(df.format(fiatAmount), currencyCode);
        order.getPurchaseUnits().add(unit);
        
        var appContext = new PayPalAppContextDTO();
        
        appContext.setReturnUrl(WEBSITE_URL + "/");
		appContext.setBrandName("Presale Round");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        order.setApplicationContext(appContext);
        OrderResponseDTO orderResponse = payPalHttpClient.createOrder(order);

        var m = PaypalTransaction.builder()
            .userId(userId)
            .txnType("PRESALE")
            .txnId(presaleOrder.getId())
            .fiatAmount(fiatAmount)
            .fiatType(currencyCode)
            .usdAmount(checkoutAmount)
            .fee(checkoutAmount - amount)
            .paypalOrderId(orderResponse.getId())
            .paypalOrderStatus(orderResponse.getStatus().toString())
            .cryptoType("NDB")
            .cryptoAmount(presaleOrder.getNdbAmount())
            .build();
        return paypalPresaleService.insert(m);
    }

    @PreAuthorize("isAuthenticated()")
    public boolean captureOrderForPresale(String orderId) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        CaptureOrderResponseDTO responseDTO = payPalHttpClient.captureOrder(orderId);
        
        if(responseDTO.getStatus() != null && responseDTO.getStatus().equals("COMPLETED")) {
			// fetch transaction
            var m = paypalPresaleService.selectByPaypalOrderId(orderId);
			if(m == null) {
                String msg = messageSource.getMessage("no_transaction", null, Locale.ENGLISH);
                throw new BidException(msg, "orderId");
            }
			if(m.getUserId() != userId) {
                String msg = messageSource.getMessage("no_match_user", null, Locale.ENGLISH);
				throw new UserNotFoundException(msg, "user");
            }

			PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(m.getTxnId());
            if(presaleOrder == null) {
                String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
                throw new BidException(msg, "orderId");
            }

            // process order
            presaleService.handlePresaleOrder(userId, m.getId(), m.getFiatAmount(), "PAYPAL", presaleOrder);
            paypalPresaleService.updateOrderStatus(m.getId(), OrderStatus.COMPLETED.toString());
			return true;
		} else return false;
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<PaypalTransaction> getAllPaypalPresaleTxns(
        int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return paypalPresaleService.selectAll(status, showStatus, offset, limit, "PRESALE", orderBy);
    }
    
    @PreAuthorize("isAuthenticated()")
    public List<PaypalTransaction> getPaypalPresaleTxnsByUser(String orderBy, int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return paypalPresaleService.selectByUser(userId, showStatus, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public PaypalTransaction getPaypalPresaleTxn(int id) {
        return paypalPresaleService.selectById(id);
    }


}
