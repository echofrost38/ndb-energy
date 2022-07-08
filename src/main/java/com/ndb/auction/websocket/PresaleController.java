package com.ndb.auction.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndb.auction.service.PresaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class PresaleController {

    @Autowired
    private PresaleOrderService presaleOrderService;

    @MessageMapping("/presale_orders/{presaleId}")
    @SendTo("/ws/presale_orders")
    public Object send(@DestinationVariable int presaleId, Principal principal, String message) throws Exception {
        return presaleOrderService.getPresaleOrders(presaleId, 0);
    }

    @MessageMapping("/presale_orders/{presaleId}/{lastOrderId}")
    @SendTo("/ws/presale_orders")
    public Object send2(@DestinationVariable int presaleId, @DestinationVariable int lastOrderId, Principal principal, String message) throws Exception {
        return presaleOrderService.getPresaleOrders(presaleId, lastOrderId);
    }

}
