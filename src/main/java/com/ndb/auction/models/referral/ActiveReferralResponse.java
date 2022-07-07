package com.ndb.auction.models.referral;

import com.ndb.auction.models.BaseModel;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveReferralResponse extends BaseModel {
    private String code;
    private String referralWallet;
    private int rate;
    private int[] commissionRate;
}
