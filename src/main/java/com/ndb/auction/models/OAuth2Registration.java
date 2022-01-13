package com.ndb.auction.models;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "OAuth2_Setting")
public class OAuth2Registration {

    private String registrationId;
    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private String redirectUriTemplate;
    private Set<String> scope;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String userNameAttributeName;
    private String jwkSetUri;
    private String clientName;

    public OAuth2Registration() {

    }

    public OAuth2Registration(
        String registrationId,
        String clientId,
        String clientSecret,
        String clientAuthenticationMethod,
        String authorizationGrantType,
        String redirectUriTemplate,
        Set<String> scope,
        String authorizationUri,
        String tokenUri,
        String userInfoUri,
        String userNameAttributeName,
        String jwkSetUri,
        String clientName
    ) {
        this.registrationId = registrationId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientAuthenticationMethod = clientAuthenticationMethod;
        this.authorizationGrantType = authorizationGrantType;
        this.redirectUriTemplate = redirectUriTemplate;
        this.scope = scope;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.userNameAttributeName = userNameAttributeName;
        this.jwkSetUri = jwkSetUri;
        this.clientName = clientName;
    }

    @DynamoDBHashKey(attributeName="registration_id")
    public String getRegistrationId() {
        return registrationId;
    }
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @DynamoDBAttribute(attributeName="client_id")
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @DynamoDBAttribute(attributeName="client_secret")
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @DynamoDBAttribute(attributeName="client_authentication_method")
    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }
    public void setClientAuthenticationMethod(String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    @DynamoDBAttribute(attributeName="authorization_grant_type")
    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }
    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    @DynamoDBAttribute(attributeName="redirect_uri_template")
    public String getRedirectUriTemplate() {
        return redirectUriTemplate;
    }
    public void setRedirectUriTemplate(String redirectUriTemplate) {
        this.redirectUriTemplate = redirectUriTemplate;
    }

    @DynamoDBAttribute(attributeName="scope")
    public Set<String> getScope() {
        return scope;
    }
    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    @DynamoDBAttribute(attributeName="authorization_uri")
    public String getAuthorizationUri() {
        return authorizationUri;
    }
    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    @DynamoDBAttribute(attributeName="token_uri")
    public String getTokenUri() {
        return tokenUri;
    }
    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    @DynamoDBAttribute(attributeName="user_info_uri")
    public String getUserInfoUri() {
        return userInfoUri;
    }
    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    @DynamoDBAttribute(attributeName="username_attribute_name")
    public String getUserNameAttributeName() {
        return userNameAttributeName;
    }
    public void setUserNameAttributeName(String userNameAttributeName) {
        this.userNameAttributeName = userNameAttributeName;
    }

    @DynamoDBAttribute(attributeName="jwt_set_uri")
    public String getJwkSetUri() {
        return jwkSetUri;
    }
    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    @DynamoDBAttribute(attributeName="client_name")
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
