package com.ndb.auction.utils;

public enum PaypalEndpoints {
    GET_ACCESS_TOKEN("/v1/oauth2/token"),
    GET_CLIENT_TOKEN("/v1/identity/generate-token"),
    ORDER_CHECKOUT("/v2/checkout/orders"),
    CREATE_WEBHOOK("/v1/notifications/webhooks"),
    CAPTURE_ORDER("/v2/checkout/orders/%s/capture"),
    CREATE_PAYOUTS("/v1/payments/payouts");

    private final String path;

    PaypalEndpoints(String path) {
        this.path = path;
    }

    public static String createUrl(String baseUrl, PaypalEndpoints endpoint) {
        return baseUrl + endpoint.path;
    }

    public static String createUrl(String baseUrl, PaypalEndpoints endpoint, String... params) {
        return baseUrl + String.format(endpoint.path, params);
    }
}
