package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.payload.response.paypal.OrderStatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paypal")
public class PaypalController extends BaseController {

	@GetMapping(value = "/auction")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
        String orderId = request.getParameter("token");
        
		// find Paypal order by ID
        PaypalAuctionTransaction m = (PaypalAuctionTransaction) paypalAuctionService.selectByOrderId(orderId);

		// update PayPal 
        paypalAuctionService.updateOrderStatus(m.getId(), OrderStatus.APPROVED.toString());

        // udpate bid ranking
        Bid bid = bidService.getBid(m.getAuctionId(), m.getUserId());
        if(bid == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(bid.isPendingIncrease()) {           
            bidService.increaseAmount(bid.getUserId(), bid.getRoundId(), bid.getTempTokenAmount(), bid.getTempTokenPrice());
            bid.setTokenAmount(bid.getTempTokenAmount());
            bid.setTokenPrice(bid.getTempTokenPrice());
        } else {

        }
        bidService.updateBidRanking(bid);
        
        return ResponseEntity.ok().body("Payment success");
    }

}