package com.ndb.auction.models.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdTokenPayload {

    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;//users unique id
    private String at_hash;
    private Long auth_time;
    private Boolean nonce_supported;
    private Boolean email_verified;
    private String email;
}
