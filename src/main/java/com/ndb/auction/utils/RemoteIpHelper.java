package com.ndb.auction.utils;

import static com.ndb.auction.utils.HttpHeader.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteIpHelper {

    private static final String UNKNOWN = "unknown";

    public static String getRemoteIpFrom(HttpServletRequest request) throws IOException {
        String ip = null;
        int tryCount = 1;

        while (!isIpFound(ip) && tryCount <= 6) {
            switch (tryCount) {
                case 1:
                    ip = request.getHeader(X_FORWARDED_FOR.key());
                    break;
                case 2:
                    ip = request.getHeader(PROXY_CLIENT_IP.key());
                    break;
                case 3:
                    ip = request.getHeader(WL_PROXY_CLIENT_IP.key());
                    break;
                case 4:
                    ip = request.getHeader(HTTP_CLIENT_IP.key());
                    break;
                case 5:
                    ip = request.getHeader(HTTP_X_FORWARDED_FOR.key());
                    break;
                default:
                    ip = request.getRemoteAddr();
            }
            tryCount++;
        }
        // check ip contains comma
        log.info("origin ip: {}", ip);
        log.info("url: {}", getUrl(request));
        log.info("data: {}",
                IOUtils.toString(new BufferedInputStream(request.getInputStream()), StandardCharsets.UTF_8));

        var ipArr = ip.split(",");
        return ipArr[0];
    }

    private static boolean isIpFound(String ip) {
        return ip != null && ip.length() > 0 && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static String getUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String uri = req.getRequestURI();
        String prmstr = req.getQueryString();
        // String uri = (String) req.getAttribute("javax.servlet.forward.request_uri");
        // String prmstr = (String)
        // req.getAttribute("javax.servlet.forward.query_string");
        return scheme + "://" + serverName + ":" + serverPort + uri + "?" + prmstr;
    }
}
