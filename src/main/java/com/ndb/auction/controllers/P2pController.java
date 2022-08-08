package com.ndb.auction.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndb.auction.hooks.BaseController;
import com.ndb.auction.web3.NDBCoinService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
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
                    .status(response.code())
                    .headers(headers)
                    .body(response.body().string());
            lastPriceTime = currentTime;
        }
        return lastPriceResponse;
    }

    @GetMapping(value = "/ndbcoin/kline/digifinex")
    public Object getNdbKlineDigifinex() throws IOException {
        long startTime = System.currentTimeMillis() - 25 * 3600 * 1000;
        Request request = new Request.Builder()
                .url("https://openapi.digifinex.com/v3/kline?symbol=NDB_USDT&period=30&start_time" + startTime)
                .build();
        Response response = new OkHttpClient().newCall(request).execute();
        if (response.code() != 200) throw new RuntimeException("RspCode=" + response.code());
        String responseString = response.body().string();
        JsonArray dataArray = JsonParser.parseString(responseString).getAsJsonObject().get("data").getAsJsonArray();
        int dataLength = dataArray.size();
        List<Double> resultList = new ArrayList<>();
        for (int i = dataLength - 48; i < dataLength; i++) {
            resultList.add(dataArray.get(i).getAsJsonArray().get(2).getAsDouble());
        }
        return resultList;
    }

    @GetMapping(value = "/ndbcoin/kline/p2p")
    public Object getNdbKlineP2p() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.p2pb2b.com/api/v2/public/market/kline?market=NDB_USDT&interval=1h&offset=0&limit=50")
                .build();
        Response response = new OkHttpClient().newCall(request).execute();
        if (response.code() != 200) throw new RuntimeException("RspCode=" + response.code());
        String responseString = response.body().string();
        JsonArray dataArray = JsonParser.parseString(responseString).getAsJsonObject().get("result").getAsJsonArray();
        int dataLength = dataArray.size();
        List<Double> resultList = new ArrayList<>();
        for (int i = dataLength - 24; i < dataLength; i++) {
            resultList.add(dataArray.get(i).getAsJsonArray().get(2).getAsDouble());
        }
        return resultList;
    }

    static long lastKlineTime;
    static Object lastKlineResponse;

    @GetMapping(value = "/ndbcoin/kline")
    public Object getNdbKline() {
        long currentTime = System.currentTimeMillis();
        if (lastKlineResponse == null || currentTime - lastKlineTime > 60000) {
            try {
                lastKlineResponse = getNdbKlineDigifinex();
                lastKlineTime = currentTime;
                return lastKlineResponse;
            } catch (Exception ex) {
                log.info("failed to get response from digifinex: {}", ex.getMessage());
            }
            try {
                lastKlineResponse = getNdbKlineP2p();
                lastKlineTime = currentTime;
                return lastKlineResponse;
            } catch (Exception ex) {
                log.info("failed to get response from p2pb2b: {}", ex.getMessage());
            }
        }
        if (lastKlineResponse == null) return ResponseEntity.status(500);
        return lastKlineResponse;
    }


    @Value("${cmc.api_key}")
    private String CMC_API_KEY;

    static long lastPriceCMCTime;
    static ResponseEntity lastPriceCMCResponse;

    @GetMapping(value = "/ndbcoin/cmc")
    public Object getNdbCmc() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (lastPriceCMCResponse == null || currentTime - lastPriceCMCTime > 600000) {
            Request request = new Request.Builder()
                    .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=NDB")
                    .header("X-CMC_PRO_API_KEY", CMC_API_KEY)
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", response.header("content-type"));

            lastPriceCMCResponse = ResponseEntity
                    .status(response.code())
                    .headers(headers)
                    .body(response.body().string());
            lastPriceCMCTime = currentTime;
        }
        return lastPriceCMCResponse;
    }

    @Autowired
    private NDBCoinService ndbCoinService;
    private static long lastNdbUsdtTime;
    private static Object lastNdbUsdtResult;

    @RequestMapping(value = "/ndbcoin/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object ndb_usdt() {
        long currentTime = System.currentTimeMillis();
        if (lastNdbUsdtResult == null || currentTime - lastNdbUsdtTime > 10 * 60000) {
            var newResult = ndbCoinService.getAll();
            if (newResult != null) {
                lastNdbUsdtResult = newResult;
                lastNdbUsdtTime = currentTime;
            }
        }
        return lastNdbUsdtResult;
    }

}
