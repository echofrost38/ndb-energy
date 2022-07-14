package com.ndb.auction.websocket;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class StompPrincipal implements Principal {

    @Getter
    private String name;

    @Getter
    @Setter
    private String email;

    public StompPrincipal(String name) {
        this.name = name;
    }

    public StompPrincipal(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Getter
    @Setter
    private Map<String, Object> attributes = new HashMap<>();

}

