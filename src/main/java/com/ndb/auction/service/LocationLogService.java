package com.ndb.auction.service;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ndb.auction.dao.LocationLogDao;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.user.UserLocationLog;
import com.ndb.auction.payload.VpnAPI;
import com.ndb.auction.utils.RemoteIpHelper;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
public class LocationLogService extends BaseService {

    @Value("${vpnapi.key}")
    private String apiKey;

    @Autowired
    private LocationLogDao locationLogDao;

    private WebClient vpnAPI;

    public LocationLogService(WebClient.Builder webClientBuilder) {
        this.vpnAPI = webClientBuilder
                .baseUrl("https://vpnapi.io/api/")
                .build();
    }

    public boolean isProxyOrVPN(UserLocationLog log) {
        return log.isVpn() || log.isProxy() || log.isTor() || log.isRelay();
    }

    public boolean isAllowedCountry(String countryCode) {
        if (countryCode == null || countryCode.isEmpty())
            return true;
        GeoLocation location = geoLocationDao.getGeoLocation(countryCode);
        if (location == null)
            return true;
        return location.isAllowed();
    }

    public UserLocationLog buildLog(HttpServletRequest request) {
        String ip = RemoteIpHelper.getRemoteIpFrom(request);
        try {
            VpnAPI response = vpnAPI.get()
                    .uri(uriBuilder -> uriBuilder.path(ip)
                            .queryParam("key", apiKey)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(VpnAPI.class).block();
            if (response == null)
                return null;
            UserLocationLog log = new UserLocationLog();
            log.setIpAddress(response.getIp());
            log.setVpn(response.getSecurity().getOrDefault("vpn", false));
            log.setProxy(response.getSecurity().getOrDefault("proxy", false));
            log.setTor(response.getSecurity().getOrDefault("tor", false));
            log.setRelay(response.getSecurity().getOrDefault("relay", false));
            log.setCity(response.getLocation().get("city"));
            log.setRegion(response.getLocation().get("region"));
            log.setCountry(response.getLocation().get("country"));
            log.setContinent(response.getLocation().get("continent"));
            log.setRegionCode(response.getLocation().get("region_code"));
            log.setCountryCode(response.getLocation().get("country_code"));
            log.setContinentCode(response.getLocation().get("continent_code"));
            log.setLatitude(Float.parseFloat(response.getLocation().get("latitude")));
            log.setLongitude(Float.parseFloat(response.getLocation().get("longitude")));
            log.setRegTime(new Timestamp(System.currentTimeMillis()));
            return log;
        } catch (WebClientException e) {
            return null;
        }
    }

    public UserLocationLog addLog(UserLocationLog log) {
        return locationLogDao.addLog(log);
    }

    public int getCountByIp(String userId, String ipAddress) {
        return locationLogDao.getCountByIp(userId, ipAddress);
    }

    public int getCountByCountryAndCity(String userId, String country, String city) {
        return locationLogDao.getCountByCountryAndCity(userId, country, city);
    }

    public UserLocationLog getLogById(String userId, String logId) {
        return locationLogDao.getLogById(userId, logId);
    }

    public List<UserLocationLog> getLogByUser(String userId) {
        return locationLogDao.getLogByUser(userId);
    }

}
