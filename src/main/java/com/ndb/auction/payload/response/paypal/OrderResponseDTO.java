package com.ndb.auction.payload.response.paypal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ndb.auction.payload.request.paypal.LinkDTO;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponseDTO {
    private String id;
    private OrderStatus status;
    private List<LinkDTO> links;
}
