package com.ndb.auction.payload.response.paypal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderDTO implements Serializable {

    public OrderDTO () {
        this.intent = OrderIntent.CAPTURE;
        this.purchaseUnits = new ArrayList<>();
    }

    private OrderIntent intent;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    @JsonProperty("application_context")
    private PayPalAppContextDTO applicationContext;
}
