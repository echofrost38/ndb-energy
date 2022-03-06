package com.ndb.auction.payload.request.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventType {
    
    public EventType(String name) {
        this.name = name;
    }

    private String name;
}
