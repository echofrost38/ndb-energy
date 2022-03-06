package com.ndb.auction.payload.request.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;

import lombok.Data;

@Data
public class PayPalAppContextDTO {
    @JsonProperty("brand_name")
    private String brandName;
    @JsonProperty("landing_page")
    private PaymentLandingPage landingPage;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("cancel_url")
    private String cancelUrl;
}
