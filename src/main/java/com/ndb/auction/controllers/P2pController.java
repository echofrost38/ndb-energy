package com.ndb.auction.controllers;

import com.ndb.auction.hooks.BaseController;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class P2pController extends BaseController {

    static long lastPriceTime;
    static ResponseEntity lastPriceResponse;

    @GetMapping(value = "/ndbcoin/price")
    public Object getNdbPrice() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (lastPriceResponse == null || currentTime - lastPriceTime > 60000) {
            Request request = new Request.Builder()
                    .url("https://api.p2pb2b.com/api/v2/public/ticker?market=NDB_USDT")
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", response.header("content-type"));

            lastPriceResponse = ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(response.body().string());
            lastPriceTime = currentTime;
        }
        return lastPriceResponse;
    }

    static long lastKlineTime;
    static ResponseEntity lastKlineResponse;

    @GetMapping(value = "/ndbcoin/kline")
    public Object getNdbKline() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (lastKlineResponse == null || currentTime - lastKlineTime > 60000) {
            Request request = new Request.Builder()
                    .url("https://api.p2pb2b.com/api/v2/public/market/kline?market=NDB_USDT&interval=1h&offset=0&limit=24")
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", response.header("content-type"));

            lastKlineResponse = ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(response.body().string());
            lastKlineTime = currentTime;
        }
        return lastKlineResponse;
    }

}
