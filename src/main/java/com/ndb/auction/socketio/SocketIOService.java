package com.ndb.auction.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.ndb.auction.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketIOService extends BaseSocketIOService {

    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SocketIOService.class);

    private final SocketIOServer socketIOServer;
    private final SocketIONamespace namespace;

    @Autowired
    private JwtUtils jwtUtils;

    public SocketIOService(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        this.namespace = socketIOServer.addNamespace("/socketio");
        this.namespace.addConnectListener(client -> {
            String token = getTokenFromUrl(client);
            if (token == null) {
                logger.info("{}    SocketIO failed to connect {}: Invalid Token", client.getHandshakeData().getUrl(), client.getRemoteAddress());
                client.disconnect();
            } else {
                try {
                    String email = jwtUtils.getEmailFromJwtToken(token);
                    clientMap.put(email, client);
                    logger.info("{}    SocketIO connected: {}:{}{}", client.getHandshakeData().getUrl(), token, email, client.getRemoteAddress());
                } catch (Exception e) {
                    logger.info("{}    SocketIO failed to connect {}{}: Invalid Token", client.getHandshakeData().getUrl(), token, client.getRemoteAddress());
                    client.disconnect();
                }
            }
        });

        this.namespace.addDisconnectListener(client -> {
            String token = getTokenFromUrl(client);
            if (token != null) {
                try {
                    String email = jwtUtils.getEmailFromJwtToken(token);
                    clientMap.remove(email);
                    logger.info("{}    SocketIO disconnected: {}:{}{}", client.getHandshakeData().getUrl(), token, email, client.getRemoteAddress());
                } catch (Exception e) {
                }
            }
//            client.disconnect();
        });

        this.namespace.addEventListener("chat", Object.class, (client, data, ackSender) -> {
            // When a client pushes a `client_info_event` event, onData accepts data, which is json data of type string here and can be Byte[], other types of object
            System.out.println(client.getSessionId().toString() + client.getHandshakeData().getUrl() + " : " + data);
        });
    }

    /**
     * Spring IoC After the container is created, start after loading the SocketIOServiceImpl Bean
     */
    @PostConstruct
    private void autoStartup() {
        start();
    }

    /**
     * Spring IoC Container closes before destroying SocketIOServiceImpl Bean to avoid restarting project service port occupancy
     */
    @PreDestroy
    private void autoStop() {
        stop();
    }

    @Override
    public void start() {
        socketIOServer.start();
    }

    @Override
    public void stop() {
        socketIOServer.stop();
    }

    public void broadcastMessage(String namespace, Object msgContent) {
        try {
            this.namespace.getBroadcastOperations().sendEvent(namespace, msgContent);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void pushMessageToUser(String email, String namespace, Object msgContent) {
        try {
            SocketIOClient client = clientMap.get(email);
            if (client == null) {
                logger.info("SocketIO {} failed to send message to {}: {} (SocketIOClient does not exist", namespace, email, msgContent);
            } else {
                client.sendEvent(namespace, msgContent);
                logger.info("SocketIO {} sent send message to {}: {}", namespace, email, msgContent);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
