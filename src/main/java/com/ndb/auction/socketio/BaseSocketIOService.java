package com.ndb.auction.socketio;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.List;
import java.util.Map;

public abstract class BaseSocketIOService {
    /**
     * Start Services
     */
    public abstract void start();

    /**
     * Out of Service
     */
    public abstract void stop();

    /**
     * Get the userId parameter in the client url (modified here to suit individual needs and client side)
     *
     * @param client: Client
     * @return: java.lang.String
     */
    public static String getTokenFromUrl(SocketIOClient client) {
        // Get the client url parameter (where userId is the unique identity)
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> userIdList = params.get("token");
        if (userIdList != null && userIdList.size() > 0) {
            return userIdList.get(0);
        }
        return null;
    }

    /**
     * Get the connected client ip address
     *
     * @param client: Client
     * @return: java.lang.String
     */
    public static String getIpByClient(SocketIOClient client) {
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        return clientIp;
    }
}