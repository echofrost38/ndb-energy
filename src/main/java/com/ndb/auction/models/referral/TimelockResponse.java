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
public class TimelockResponse extends BaseModel {
    private Boolean status;
    private int hours;
    private int minutes;
    private int seconds;
}
