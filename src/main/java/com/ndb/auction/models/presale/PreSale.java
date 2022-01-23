package com.ndb.auction.models.presale;

import java.util.List;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class PreSale extends BaseModel {
    
    public static int PENDING = 0;
    public static int STARTED = 1;
    public static int ENDED = 2;

    private int round;
    
    private Long startedAt;
    private Long endedAt;
    
    private Long tokenAmount;
    private Long tokenPrice;
    private Long sold;

    private int status;

    private List<PreSaleCondition> conditions;

    public PreSale(
        int round,
        Long startedAt,
        Long endedAt, 
        Long tokenAmount, 
        Long tokenPrice, 
        List<PreSaleCondition> conditions
    ) {
        this.round = round;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.tokenAmount = tokenAmount;
        this.tokenPrice = tokenPrice;
        this.conditions = conditions;
    }

}
