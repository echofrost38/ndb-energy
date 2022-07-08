package com.ndb.auction.hooks;

import com.ndb.auction.models.LocationLog;
import com.ndb.auction.utils.RemoteIpHelper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

/**
 * TODOs
 * 1. processing lack of payment!!!
 */

@RestController
@RequestMapping("/")
public class LocationController extends BaseController {

    private static final String SESSION_LOCATION = "location";

    private boolean checkLocation(LocationLog location) {
        if (location == null) return true;
        if (location.getCountryCode().equals("US") || location.getCountryCode().equals("CA")) return false;
        return true;
    }

    @GetMapping("/location")
    public Object getLocation(HttpServletRequest request) throws IOException {
        String ip = RemoteIpHelper.getRemoteIpFrom(request);
        HttpSession session = request.getSession(true);
        LocationLog location = (LocationLog) session.getAttribute(SESSION_LOCATION);
        if (location == null || !ip.equals(location.getIpAddress())) {
            location = locationLogService.buildLog(ip);
            session.setAttribute(SESSION_LOCATION, location);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", checkLocation(location));
        return resultMap;
    }

}
