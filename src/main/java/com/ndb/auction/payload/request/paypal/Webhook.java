package com.ndb.auction.payload.request.paypal;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Webhook {

    public Webhook(String url, List<EventType> events) {
        this.url = url;
        this.event_types = events;
    }

    private String url;
    private List<EventType> event_types;
}
