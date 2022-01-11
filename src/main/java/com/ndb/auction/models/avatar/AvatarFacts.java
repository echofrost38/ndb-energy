package com.ndb.auction.models.avatar;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarFacts extends BaseModel{
    private int profileId;
    private String topic;
    private String detail;
}
