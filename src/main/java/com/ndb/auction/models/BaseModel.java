package com.ndb.auction.models;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class BaseModel {

    protected int id;
    protected Timestamp regDate;
    protected Timestamp updateDate;
    protected int deleted;

}
