package com.ndb.auction.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class ServerMaintenance extends BaseModel {

    private String message;
    private Timestamp expireDate;

}
