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
public class PriceController extends BaseController {

    static long lastTime;
    static ResponseEntity<Object> lastResponse;


    @GetMapping(value = "/price/ndb")
    public Object getPriceData() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (lastResponse == null || currentTime - lastTime > 60000) {
            Request request = new Request.Builder()
                    .url("https://api.p2pb2b.com/api/v2/public/ticker?market=NDB_USDT")
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", response.header("content-type"));

            lastResponse = ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(response.body().string());
            lastTime = currentTime;
        }
        return lastResponse;
    }

}
