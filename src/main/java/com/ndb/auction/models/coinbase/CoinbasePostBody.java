package com.ndb.auction.models.coinbase;

public class CoinbasePostBody {
    
    private String name;
    private String description;
    private String pricing_type;
    private CoinbasePricing local_price;

    public CoinbasePostBody (
        String name, 
        String description,
        String pricingType,
        Double price
    ) {
        this.name = name;
        this.description = description;
        this.pricing_type = pricingType;
        this.local_price = new CoinbasePricing();
        local_price.setCurrency("usd");
        local_price.setAmount(price.toString());
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPricing_type() {
        return pricing_type;
    }
    public void setPricing_type(String pricing_type) {
        this.pricing_type = pricing_type;
    }
    public CoinbasePricing getLocal_price() {
        return local_price;
    }
    public void setLocal_price(CoinbasePricing local_price) {
        this.local_price = local_price;
    }

    
}
