package com.ndb.auction.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class P2pService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public P2pService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void sendMessage() throws IOException {
        JsonObject resultObject = new JsonObject();
        String tickerText = null;
        String klineText = null;
        try {
            Request request = new Request.Builder()
                    .url("https://api.p2pb2b.com/api/v2/public/ticker?market=NDB_USDT")
                    .build();
            Response response = new  OkHttpClient().newCall(request).execute();
            tickerText = response.body().string();
            JsonObject root = JsonParser.parseString(tickerText).getAsJsonObject();
            resultObject.add("ticker", root.get("result"));
        } catch (JsonSyntaxException e) {
            resultObject.addProperty("ticker", tickerText == null ? e.toString() : tickerText);
        } catch (Exception e) {
            resultObject.addProperty("ticker", e.toString());
        }
        try {
            Request request = new Request.Builder()
                    .url("https://api.p2pb2b.com/api/v2/public/market/kline?market=NDB_USDT&interval=1h&offset=0&limit=24")
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();
            klineText = response.body().string();
            JsonObject root = JsonParser.parseString(klineText).getAsJsonObject();
            resultObject.add("kline", root.get("result"));
        } catch (JsonSyntaxException e) {
            resultObject.addProperty("kline", klineText == null ? e.toString() : klineText);
        } catch (Exception e) {
            resultObject.addProperty("kline", e.toString());
        }
        simpMessagingTemplate.convertAndSend("/ws/ndbcoin", resultObject.toString());
    }

}
