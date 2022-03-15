package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeCustomer {
    
    public StripeCustomer(
        int userId, 
        String customerId,
        String paymentMethod,
        String brand,
        String country, 
        Long expMonth,
        Long expYear,
        String last4) 
    {
        this.userId = userId;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.brand = brand;
        this.country = country;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.last4 = last4;
    }
    
    private int id;
    private int userId;

    // Customer details
    private String customerId;

    // Card details
    private String brand;
    private String country;
    private Long expMonth;
    private Long expYear;
    private String last4;
    private String paymentMethod;
}
