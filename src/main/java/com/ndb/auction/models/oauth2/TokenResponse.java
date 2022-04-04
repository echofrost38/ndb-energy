package com.ndb.auction.models.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {

    private String access_token;
    private String token_type;
    private Long expires_in;
    private String refresh_token;
    private String id_token;
}
