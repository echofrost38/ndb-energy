package com.ndb.auction.utils;

import com.google.gson.Gson;
import com.ndb.auction.payload.CoinPrice;
import com.ndb.auction.payload.response.FiatConverted;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ThirdAPIUtils {

    private WebClient binanceAPI;
    private WebClient xchangeAPI;

    private static Gson gson = new Gson();

    public ThirdAPIUtils (WebClient.Builder webClientBuilder) {
        this.binanceAPI = webClientBuilder
            .baseUrl("https://api.binance.com/api/v3")
            .build();
        this.xchangeAPI = webClientBuilder
            .baseUrl("https://api.exchangerate.host")
            .build();
    }

    public double getCryptoPriceBySymbol(String symbol) {
        try {
            if(symbol.equals("USDT")) {
                return 1.0;
            }

            String symbolPair = symbol + "USDT";
            CoinPrice objs = binanceAPI.get()
                    .uri(uriBuilder -> uriBuilder.path("/ticker/price")
                            .queryParam("symbol", symbolPair.toUpperCase())
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .onStatus(org.springframework.http.HttpStatus::is4xxClientError, response -> {
                        return null;
                    })
                    .bodyToMono(CoinPrice.class)
                    .onErrorMap(throwable -> {
                        return null;
                    })
                    .block();
            return Double.valueOf(objs.getPrice());
        } catch (Exception e) {
        }
        return 0.0;
    }

    public double currencyConvert(String from, String to, double amount) {
        try {
            String converted = xchangeAPI.get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                    .queryParam("from", from)
                    .queryParam("to", to)
                    .queryParam("amount", amount)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            FiatConverted fiatConverted = gson.fromJson(converted, FiatConverted.class);
            return fiatConverted.getResult();
        } catch (Exception e) {
        
        }
        return 0.0;
    }
}
