package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class NotificationType {

    private int nType;
    private String tName;
    private boolean broadcast;
    
}
