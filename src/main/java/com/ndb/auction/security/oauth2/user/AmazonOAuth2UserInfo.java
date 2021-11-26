package com.ndb.auction.security.oauth2.user;

import java.util.Map;

public class AmazonOAuth2UserInfo extends OAuth2UserInfo {

    public AmazonOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getLocale() {
        // TODO Auto-generated method stub
        return null;
    }
}
