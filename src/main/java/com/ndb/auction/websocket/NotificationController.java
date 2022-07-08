package com.ndb.auction.websocket;

import com.ndb.auction.service.NotificationService;
import com.ndb.auction.service.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/notify")
    @SendTo("/ws/notify")
    public Object send(Principal principal, String message) throws Exception {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            int userId = userDetails.getId();
//            return notificationService.getPaginatedNotifications(userId, offset, limit);
        }catch (Exception e){
        }
        return "sdf";
    }

//    @MessageMapping("/notify/{presaleId}")
//    @SendTo("/ws/presale_orders")
//    public Object send(@DestinationVariable int presaleId, Principal principal, String message) throws Exception {
//        JsonObject requestJson = JsonParser.parseString(message).getAsJsonObject();
//        int presaleId = requestJson.get("presaleId").getAsInt();
//        int lastOrderId = requestJson.get("lastOrderId").getAsInt();
//        System.out.println(message);
//        Map<String, Object> map = ((StompPrincipal) principal).getAttributes();
//        System.out.println(map);
//        map.put(message, message);
//        return presaleOrderService.getPresaleOrders(presaleId, lastOrderId);
//    }

}
