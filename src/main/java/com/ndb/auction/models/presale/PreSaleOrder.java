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
    
    public PreSaleOrder(int userId, int presaleId, Long ndbAmount, Long ndbPrice, int destination, String extAddr) {
        this.userId = userId;
        this.presaleId = presaleId;
        this.ndbAmount = ndbAmount;
        this.ndbPrice = ndbPrice;
        this.destination = destination;
        this.extAddr = extAddr;
    }

    public static int INTERNAL = 1;
    public static int EXTERNAL = 2;

    private int userId;
    private int presaleId;

    private int destination;
    private String extAddr;

    private Long ndbAmount;
    private Long ndbPrice;

    private int status;

    private Long createdAt;
    private Long updatedAt;

}
