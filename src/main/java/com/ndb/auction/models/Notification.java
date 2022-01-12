package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseModel {

    private int userId;
    private Long timeStamp;
    private int nType;
    private boolean read;
    private String title;
    private String msg;

    public Notification(int userId, int type, String title, String msg) {
        this.userId = userId;
        this.nType = type;
        this.read = false;
        this.title = title;
        this.msg = msg;
    }

}
