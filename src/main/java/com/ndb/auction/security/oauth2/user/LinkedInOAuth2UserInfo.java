package com.ndb.auction.security.oauth2.user;

import java.util.Map;
public class LinkedInOAuth2UserInfo extends OAuth2UserInfo {

	public LinkedInOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getName() {
		return ((String) attributes.get("localizedFirstName")).concat(" ").concat((String) attributes.get("localizedLastName"));
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("emailAddress");
	}

	@Override
	public String getImageUrl() {
		return (String) attributes.get("pictureUrl");
	}

    @Override
    public String getLocale() {
        return null;
    }
}