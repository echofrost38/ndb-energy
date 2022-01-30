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

    public static final int BID_RANKING_UPDATED = 0;
    public static final int NEW_ROUND_STARTED = 1;
    public static final int ROUND_FINISHED = 2;
    public static final int BID_CLOSED = 3;
    public static final int PAYMENT_RESULT = 4;
    public static final int KYC_VERIFIED = 5;
    public static final int DEPOSIT_SUCCESS = 6;
    public static final int WITHDRAW_SUCCESS = 7;

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
