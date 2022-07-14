package com.ndb.auction.utils;

import static com.ndb.auction.utils.HttpHeader.*;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

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
        var ipArr = ip.split(",");
        return ipArr[0];
    }

    private static boolean isIpFound(String ip) {
        return ip != null && ip.length() > 0 && !UNKNOWN.equalsIgnoreCase(ip);
    }

}
