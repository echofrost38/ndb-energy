package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseModel {

    protected int id;
    protected long regDate;
    protected long updateDate;
    protected int deleted;

}
