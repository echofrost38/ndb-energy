package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameResolver;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class NotificationType extends BaseModel {

    private int nType;
    private String tName;
    private boolean broadcast;

    public NotificationType(int nType, String tName, boolean broadcast) {
        this.nType = nType;
        this.tName = tName;
        this.broadcast = broadcast;
    }

}
