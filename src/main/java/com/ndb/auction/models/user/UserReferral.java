package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReferral extends BaseModel {
    private String referralCode;
    private String referredByCode;
    private String walletConnect;
    private int rate;
    private int[] commissionRate;
    private String paidTxn;
    private boolean active;
    private boolean record;
}
