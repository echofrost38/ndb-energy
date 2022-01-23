package com.ndb.auction.models.presale;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class PreSaleOrder extends BaseModel {
    
    private int roundId;
    private int userId;

    private Long ndbAmount;
    private Long ndbPrice;

    private Long createdAt;
    private Long updatedAt;

}
