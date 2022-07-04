package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.LocationLogDao;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.LocationLog;
import com.ndb.auction.payload.VpnAPI;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
@Slf4j
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

    public boolean isProxyOrVPN(LocationLog log) {
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

    public LocationLog buildLog(String ip) {
        try {
            log.info("ip address: {}", ip);
            VpnAPI response = vpnAPI.get()
                    .uri(uriBuilder -> uriBuilder.path(ip)
                            .queryParam("key", apiKey)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(VpnAPI.class).block();
            if (response == null)
                return null;
            LocationLog log = new LocationLog();
            if(response.getSecurity() == null) return null;
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
            return log;
        } catch (WebClientException e) {
            return null;
        }
    }

    public LocationLog addLog(LocationLog log) {
        return locationLogDao.addLog(log);
    }

    public int getCountByIp(int userId, String ipAddress) {
        return locationLogDao.getCountByIp(userId, ipAddress);
    }

    public int getCountByCountryAndCity(int userId, String country, String city) {
        return locationLogDao.getCountByCountryAndCity(userId, country, city);
    }

    public LocationLog getLogById(int id) {
        return locationLogDao.getLogById(id);
    }

    public List<LocationLog> getLogByUser(int userId) {
        return locationLogDao.getLogByUser(userId);
    }

}
