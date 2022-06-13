package com.ndb.auction.resolver.payment.presale;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
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

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresalePaypal extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    private final PaypalHttpClient payPalHttpClient;

	@Autowired
	public PresalePaypal(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}

    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO paypalForPresale(int presaleId, int orderId, String currencyCode) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        
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
        
        var checkoutAmount = getPayPalTotalOrder(userId, amount);
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

        var m = new PaypalPresaleTransaction(userId, presaleId, orderId, fiatAmount, currencyCode, amount, checkoutAmount - amount,
            orderResponse.getId(), orderResponse.getStatus().toString());
        return paypalPresaleService.insert(m);
    }

    @PreAuthorize("isAuthenticated()")
    public boolean captureOrderForPresale(String orderId) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        CaptureOrderResponseDTO responseDTO = payPalHttpClient.captureOrder(orderId);
        
        if(responseDTO.getStatus() != null && responseDTO.getStatus().equals("COMPLETED")) {
			// fetch transaction
            PaypalPresaleTransaction m = (PaypalPresaleTransaction) paypalPresaleService.selectByPaypalOrderId(orderId);
			if(m == null) {
                String msg = messageSource.getMessage("no_transaction", null, Locale.ENGLISH);
                throw new BidException(msg, "orderId");
            }
			if(m.getUserId() != userId) {
                String msg = messageSource.getMessage("no_match_user", null, Locale.ENGLISH);
				throw new UserNotFoundException(msg, "user");
            }

			PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(m.getOrderId());
            if(presaleOrder == null) {
                String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
                throw new BidException(msg, "orderId");
            }

            // process order
            presaleService.handlePresaleOrder(userId, m.getId(), m.getAmount(), "PAYPAL", presaleOrder);
            paypalPresaleService.updateOrderStatus(m.getId(), OrderStatus.COMPLETED.toString());
			return true;
		} else return false;
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<PaypalPresaleTransaction> getAllPaypalPresaleTxns(String orderBy) {
        return (List<PaypalPresaleTransaction>) paypalPresaleService.selectAll(orderBy);
    }
    
    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalPresaleTransaction> getPaypalPresaleTxnsByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<PaypalPresaleTransaction>) paypalPresaleService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public PaypalPresaleTransaction getPaypalPresaleTxn(int id) {
        return (PaypalPresaleTransaction) paypalPresaleService.selectById(id);
    }


}
