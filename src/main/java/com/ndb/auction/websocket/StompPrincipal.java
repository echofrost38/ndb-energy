package com.ndb.auction.websocket;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class StompPrincipal implements Principal {

    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Getter
    @Setter
    private Map<String, Object> attributes = new HashMap<>();

}

