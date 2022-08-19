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
public class UserSocial extends BaseModel {
    private transient int id;
    private String discord;
    private transient String tier;
}
