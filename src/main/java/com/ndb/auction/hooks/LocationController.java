package com.ndb.auction.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ndb.auction.exceptions.IPNExceptions;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.LocationLog;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.CryptoTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.security.jwt.AuthTokenFilter;
import com.ndb.auction.utils.RemoteIpHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * TODOs
 * 1. processing lack of payment!!!
 */

@RestController
@RequestMapping("/")
@Slf4j
public class LocationController extends BaseController {

    private static final String SESSION_IP_LOCATION = "ip_location";

    private boolean checkLocation(LocationLog log) {
        if (log == null) return true;
        if (log.getCountryCode().equals("US") || log.getCountryCode().equals("CA")) return false;
        return true;
    }

    @GetMapping("/location")
    public Object getLocation(HttpServletRequest request) {
        String ip = RemoteIpHelper.getRemoteIpFrom(request);
        LocationLog log;
        HttpSession session = request.getSession(true);
        Map<String, Object> locationMap = (Map<String, Object>) session.getAttribute(SESSION_IP_LOCATION);
        if (locationMap == null) {
            locationMap = new HashMap<>();
            log = locationLogService.buildLog(ip);
            locationMap.put(ip, log);
        } else if ((log = (LocationLog) locationMap.get(ip)) == null) {
            log = locationLogService.buildLog(ip);
            locationMap.put(ip, log);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", checkLocation(log));
        return resultMap;
    }

}
