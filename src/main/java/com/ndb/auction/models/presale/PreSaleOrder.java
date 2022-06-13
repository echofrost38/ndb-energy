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
    
    public PreSaleOrder(int userId, int presaleId, double ndbAmount, double ndbPrice, int destination, String extAddr) {
        this.userId = userId;
        this.presaleId = presaleId;
        this.ndbAmount = ndbAmount;
        this.ndbPrice = ndbPrice;
        this.destination = destination;
        this.extAddr = extAddr;
        this.status = 0;
        this.prefix = null;
        this.name = null;
        this.paidAmount = 0;
        this.paymentId = 0;
        this.paymentType = "";
    }

    public static int INTERNAL = 1;
    public static int EXTERNAL = 2;

    private int userId;
    private int presaleId;

    private String prefix;
    private String name;

    private int destination;
    private String extAddr;

    private double ndbAmount;
    private double ndbPrice;

    // for confirmed payment information
    private String paymentType;
    private int paymentId;
    private double paidAmount;

    private int status;

    private Long createdAt;
    private Long updatedAt;

}
