package com.ndb.auction.utils;

import static com.ndb.auction.utils.PaypalEndpoints.CREATE_PAYOUTS;
import static com.ndb.auction.utils.PaypalEndpoints.GET_ACCESS_TOKEN;
import static com.ndb.auction.utils.PaypalEndpoints.GET_CLIENT_TOKEN;
import static com.ndb.auction.utils.PaypalEndpoints.ORDER_CHECKOUT;
import static com.ndb.auction.utils.PaypalEndpoints.CREATE_WEBHOOK;
import static com.ndb.auction.utils.PaypalEndpoints.CAPTURE_ORDER;
import static com.ndb.auction.utils.PaypalEndpoints.createUrl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.config.PaypalConfig;
import com.ndb.auction.payload.request.paypal.EventType;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayoutsDTO;
import com.ndb.auction.payload.request.paypal.Webhook;
import com.ndb.auction.payload.response.paypal.AccessTokenResponseDTO;
import com.ndb.auction.payload.response.paypal.CaptureOrderResponseDTO;
import com.ndb.auction.payload.response.paypal.ClientTokenDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.PayoutResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class PaypalHttpClient {
    private final HttpClient httpClient;
    private final PaypalConfig paypalConfig;
    private final ObjectMapper objectMapper;

    @Value("${paypal.callbackUrl}")
    private String PAYPAL_CALLBACK_URL;

    @Autowired
    public PaypalHttpClient(PaypalConfig paypalConfig, ObjectMapper objectMapper) throws Exception {
        this.paypalConfig = paypalConfig;
        this.objectMapper = objectMapper;
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        var url = PAYPAL_CALLBACK_URL + "/auction";
        var events = new ArrayList<EventType>();
        EventType batchSuccess = new EventType("PAYMENT.PAYOUTSBATCH.SUCCESS");
        EventType batchDenied = new EventType("PAYMENT.PAYOUTSBATCH.DENIED");
        EventType orderCompleted = new EventType("CHECKOUT.ORDER.COMPLETED");
        events.add(batchSuccess);
        events.add(batchDenied);
        events.add(orderCompleted);
        Webhook webhook = new Webhook(url, events);
        createWebhook(webhook);
    }

    public AccessTokenResponseDTO getAccessToken() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), GET_ACCESS_TOKEN)))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, encodeBasicCredentials())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, AccessTokenResponseDTO.class);
    }

    public ClientTokenDTO getClientToken() throws Exception {
        var accessTokenDto = getAccessToken();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), GET_CLIENT_TOKEN)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();

        return objectMapper.readValue(content, ClientTokenDTO.class);
    }

    public OrderResponseDTO createOrder(OrderDTO orderDTO) throws Exception {
        var accessTokenDto = getAccessToken();
        var payload = objectMapper.writeValueAsString(orderDTO);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), ORDER_CHECKOUT)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, OrderResponseDTO.class);
    }

    public PayoutResponseDTO createPayout(PayoutsDTO payoutDTO) throws Exception {
        var accessTokenDto = getAccessToken();
        var payload = objectMapper.writeValueAsString(payoutDTO);
        var request = HttpRequest.newBuilder()
            .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), CREATE_PAYOUTS)))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, PayoutResponseDTO.class);
    }

    public Object createWebhook(Webhook webhook) throws Exception {
        var accessTokenDto = getAccessToken();
        var payload = objectMapper.writeValueAsString(webhook);
        var request = HttpRequest.newBuilder()
            .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), CREATE_WEBHOOK)))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, Object.class);
    }

    public CaptureOrderResponseDTO captureOrder(String id) throws Exception {
        var accessTokenDto = getAccessToken();
        var payload = "";
        var request = HttpRequest.newBuilder()
            .uri(URI.create(createUrl(paypalConfig.getBaseUrl(), CAPTURE_ORDER, id)))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, CaptureOrderResponseDTO.class);
    }


    private String encodeBasicCredentials() {
        var input = paypalConfig.getClientId() + ":" + paypalConfig.getClientSecret();
        return "Basic " + Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
