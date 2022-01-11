package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Tier {

    private int level;
    private String name;
    private long point;

    public Tier(int level, String name, long point) {
        this.level = level;
        this.name = name;
        this.point = point;
    }

}
