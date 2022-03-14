package com.ndb.auction.payload.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
public class FreaksResponse {
    private String date;
    private String base;
    private Map<String, String> rates;
}
