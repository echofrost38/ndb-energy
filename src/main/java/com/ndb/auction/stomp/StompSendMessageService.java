package com.ndb.auction.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StompSendMessageService {

    private static final Logger logger = LoggerFactory.getLogger(StompSendMessageService.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(String destination, Object payload) {
        try {
            simpMessagingTemplate.convertAndSend(destination, payload);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
