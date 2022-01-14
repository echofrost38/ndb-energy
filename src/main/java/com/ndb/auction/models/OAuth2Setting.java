package com.ndb.auction.models;

import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class OAuth2Setting extends BaseModel {

    public static final String SCOPE_SEPARATOR = ";";

    private String clientId;
    private String clientSecret;
    private String clientAuthMethod;
    private String authGrantType;
    private String redirectUriTemplate;
    private Set<String> scope;
    private String authUri;
    private String tokenUri;
    private String userInfoUri;
    private String userAttributeName;
    private String jwkSetUri;
    private String clientName;

    public OAuth2Setting(
            int id,
            String clientId,
            String clientSecret,
            String clientAuthMethod,
            String authGrantType,
            String redirectUriTemplate,
            Set<String> scope,
            String authUri,
            String tokenUri,
            String userInfoUri,
            String userAttributeName,
            String jwkSetUri,
            String clientName) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientAuthMethod = clientAuthMethod;
        this.authGrantType = authGrantType;
        this.redirectUriTemplate = redirectUriTemplate;
        this.scope = scope;
        this.authUri = authUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.userAttributeName = userAttributeName;
        this.jwkSetUri = jwkSetUri;
        this.clientName = clientName;
    }

    public String getScopeString() {
        return String.join(SCOPE_SEPARATOR, this.scope);
    }

    public void setScope(String scope) {
        this.scope = Set.of(scope.split(SCOPE_SEPARATOR));
    }

}
