package com.ndb.auction.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class PresaleController {

    @MessageMapping("/presale_orders")
    @SendTo("/ws/presale_orders")
    public String send2(Principal principal, String message) throws Exception {
        System.out.println(message);
        Map<String, Object> map = ((StompPrincipal) principal).getAttributes();
        System.out.println(map);
        map.put(message, message);
        return principal.getName() + " : " + message;
    }

}
