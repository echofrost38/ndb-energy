package com.ndb.auction.payload.response.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoneyDTO {

    public MoneyDTO(String value, String currencyCode) {
        this.currencyCode = currencyCode;
        this.value = value;
    }

    @JsonProperty("currency_code")
    private String currencyCode;
    private String value;
}