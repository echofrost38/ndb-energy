package com.ndb.auction.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.models.SlackMessage;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class SlackUtils {

    @Value("${bsc.json.chainid}")
    private long bscChainId;
    @Value("${slack.webhook.ndbtoken}")
    private String[] slackWebhookToken;

    @Value("${slack.webhook.referral}")
    private String[] slackWebhookReferral;

    public enum SlackChannel {
        TOKEN,
        REFERRAL
    }

    public void sendMessage(SlackMessage message,SlackChannel channel) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = null;
            switch (channel) {
                case TOKEN:
                    httpPost = new HttpPost((bscChainId == 56 ? slackWebhookToken[0] : slackWebhookToken[1]));
                    break;
                case REFERRAL:
                    httpPost = new HttpPost((bscChainId == 56 ? slackWebhookReferral[0] : slackWebhookReferral[1]));
                    break;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(message);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}