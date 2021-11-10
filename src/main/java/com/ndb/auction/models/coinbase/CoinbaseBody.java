package com.ndb.auction.models.coinbase;

import java.util.List;
import java.util.Map;

public class CoinbaseBody {
    
    private Map<String, String> addresses;
    private String brand_color;
    private String brand_logo_url;
    private String code;
    private String created_at;
    private String description;
    private Map<String, String> exchange_rates;
    private String expires_at;
    private String fees_settled;
    private String hosted_url;
    private String id;
    private Map<String, String> local_exchange_rates;
    private String logo_url;
    private Map<String, String> metadata;
    private String name;
    private PaymentThreshold payment_threshold;
    private Map<String, String> payments;
    private Map<String, CoinbasePricing> pricing;
    private String pricing_type;
    private boolean pwcb_only;
    private String resource;
    private String support_email;
    private List<CoinbaseTimeline> timeline;
    private boolean utxo;
    
    public Map<String, String> getAddresses() {
        return addresses;
    }
    public void setAddresses(Map<String, String> addresses) {
        this.addresses = addresses;
    }
    public String getBrand_color() {
        return brand_color;
    }
    public void setBrand_color(String brand_color) {
        this.brand_color = brand_color;
    }
    public String getBrand_logo_url() {
        return brand_logo_url;
    }
    public void setBrand_logo_url(String brand_logo_url) {
        this.brand_logo_url = brand_logo_url;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Map<String, String> getExchange_rates() {
        return exchange_rates;
    }
    public void setExchange_rates(Map<String, String> exchange_rates) {
        this.exchange_rates = exchange_rates;
    }
    public String getExpires_at() {
        return expires_at;
    }
    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }
    public String getFees_settled() {
        return fees_settled;
    }
    public void setFees_settled(String fees_settled) {
        this.fees_settled = fees_settled;
    }
    public String getHosted_url() {
        return hosted_url;
    }
    public void setHosted_url(String hosted_url) {
        this.hosted_url = hosted_url;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Map<String, String> getLocal_exchange_rates() {
        return local_exchange_rates;
    }
    public void setLocal_exchange_rates(Map<String, String> local_exchange_rates) {
        this.local_exchange_rates = local_exchange_rates;
    }
    public String getLogo_url() {
        return logo_url;
    }
    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PaymentThreshold getPayment_threshold() {
        return payment_threshold;
    }
    public void setPayment_threshold(PaymentThreshold payment_threshold) {
        this.payment_threshold = payment_threshold;
    }
    public Map<String, String> getPayments() {
        return payments;
    }
    public void setPayments(Map<String, String> payments) {
        this.payments = payments;
    }
    public Map<String, CoinbasePricing> getPricing() {
        return pricing;
    }
    public void setPricing(Map<String, CoinbasePricing> pricing) {
        this.pricing = pricing;
    }
    public String getPricing_type() {
        return pricing_type;
    }
    public void setPricing_type(String pricing_type) {
        this.pricing_type = pricing_type;
    }
    public boolean isPwcb_only() {
        return pwcb_only;
    }
    public void setPwcb_only(boolean pwcb_only) {
        this.pwcb_only = pwcb_only;
    }
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getSupport_email() {
        return support_email;
    }
    public void setSupport_email(String support_email) {
        this.support_email = support_email;
    }
    public List<CoinbaseTimeline> getTimeline() {
        return timeline;
    }
    public void setTimeline(List<CoinbaseTimeline> timeline) {
        this.timeline = timeline;
    }
    public boolean isUtxo() {
        return utxo;
    }
    public void setUtxo(boolean utxo) {
        this.utxo = utxo;
    }


}
