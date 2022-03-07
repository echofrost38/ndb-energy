package com.ndb.auction.payload.response.paypal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ndb.auction.payload.response.paypal.BatchHeader.BatchHeader;
import com.ndb.auction.payload.response.paypal.orders.PurchaseUnit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Resource {
    private String id;
    private String status;
    private Amount amount;
    private BatchHeader batch_header;
    private List<PurchaseUnit> purchase_units;
}
