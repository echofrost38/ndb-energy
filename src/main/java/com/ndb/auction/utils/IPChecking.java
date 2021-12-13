package com.ndb.auction.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.google.common.net.HttpHeaders;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.payload.IPLocation;
import com.ndb.auction.service.BaseService;

import reactor.core.publisher.Mono;

@Service
public class IPChecking extends BaseService {
	
	@Value("${free.geolocation.apikey}")
	private String apiKey;
	private WebClient ipGeolocation;
	
	public IPChecking(WebClient.Builder webClientBuilder) {
		this.ipGeolocation = webClientBuilder
				.baseUrl("https://api.freegeoip.app/json")
				.build();
	}
	
	private IPLocation getIpLocation(String ip) {
		Mono<IPLocation> ipLocation = ipGeolocation.get()
				.uri(uriBuilder -> uriBuilder.path("/"+ip)
						.queryParam("apikey", apiKey)
						.build())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve()
				.bodyToMono(IPLocation.class)
				.onErrorResume(WebClientResponseException.class,
				          ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex));
		return ipLocation.block();
				
	}
	
	public Boolean isAllowed(String ip) {
		String countryCode = getIpLocation(ip).getCountry_code();	
		// Get Location 
		GeoLocation location = geoLocationDao.getGeoLocation(countryCode);
		if(location == null) return true;
		return location.isAllowed();
	}
}
