package com.ndb.auction.resolver.payment.presale;

import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
import com.ndb.auction.payload.response.paypal.CaptureOrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderStatus;
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
            throw new BidException("There is no presale order.", "orderId");
        }
        Long amount = presaleOrder.getNdbAmount() * presaleOrder.getNdbPrice();
        PaypalPresaleTransaction m = new PaypalPresaleTransaction(userId, presaleId, orderId, amount, null, null);

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
			if(m == null) throw new BidException("There is no transaction", "orderId");
			if(m.getUserId() != userId) {
				throw new UserNotFoundException("User doesn't match.", "user");
            }

			PreSaleOrder presaleOrder = presaleOrderService.getPresaleById(m.getOrderId());
            if(presaleOrder == null) {
                throw new BidException("There is no presale order", "orderId");
            }

            // process order
            presaleService.handlePresaleOrder(userId, presaleOrder);
            paypalPresaleService.updateOrderStatus(m.getId(), OrderStatus.COMPLETED.toString());
			return true;
		} else return false;
    }
    
}
