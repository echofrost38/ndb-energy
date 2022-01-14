package com.ndb.auction.service.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndb.auction.models.user.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserDetailsImpl implements OAuth2User, UserDetails {

	private static final long serialVersionUID = 5558252995866998438L;

	private int id;
	private String username;
	private String email;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private String ipAddress;

	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public UserDetailsImpl() {
	}

	public UserDetailsImpl(
			int id,
			String username,
			String email,
			String password,
			String ipAddress,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.ipAddress = ipAddress;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRole().stream()
				.map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());
		return new UserDetailsImpl(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getPassword(),
				null,
				authorities);
	}

	public static UserDetailsImpl build(User user, Map<String, Object> attributes) {
		UserDetailsImpl userPrincipal = UserDetailsImpl.build(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	public int getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}

}
