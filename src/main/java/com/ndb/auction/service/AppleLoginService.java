package com.ndb.auction.service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ndb.auction.models.oauth2.IdTokenPayload;
import com.ndb.auction.models.oauth2.TokenResponse;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;
import java.util.Date;

public class AppleLoginService {

    private static final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/token";

    private static final String KEY_ID = "BV8SML3NAY";
    private static final String TEAM_ID = "33T4RDL646";
    private static final String CLIENT_ID = "com.ndb.auth";

    private static PrivateKey pKey;

    private static PrivateKey getPrivateKey() throws Exception {

        Resource privateKeyFile = new ClassPathResource("apple/AuthKey_BV8SML3NAY.p8");
        Reader targetReader = new InputStreamReader(privateKeyFile.getInputStream());

        final PEMParser pemParser = new PEMParser(targetReader);
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        return converter.getPrivateKey(object);
    }

    private static String generateJWT() throws Exception {
        if (pKey == null) {
            pKey = getPrivateKey();
        }

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, KEY_ID)
                .setIssuer(TEAM_ID)
                .setAudience("https://appleid.apple.com")
                .setSubject(CLIENT_ID)
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.ES256, pKey)
                .compact();
    }

    public static String appleAuth(String authorizationCode) throws Exception {

        HttpResponse<String> response = Unirest.post(APPLE_AUTH_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", CLIENT_ID)
                .field("client_secret", generateJWT())
                .field("grant_type", "authorization_code")
                .field("code", authorizationCode)
                .asString();

        TokenResponse tokenResponse=new Gson().fromJson(response.getBody(),TokenResponse.class);
        String idToken = tokenResponse.getId_token();
        // first index ([0]) is header, can be ignored
        String payload = idToken.split("\\.")[1];
        String decoded = new String(Decoders.BASE64.decode(payload));

        IdTokenPayload idTokenPayload = new Gson().fromJson(decoded,IdTokenPayload.class);

        return idTokenPayload.getSub();
    }

}