package com.ndb.auction.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ndb.auction.models.user.User;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${zendesk.shared.secret}")
	private String ZENDESK_SHARED_SECRET;
	
	public String generateJwtToken(Authentication authentication) {
		String email = "";
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();	
			email = userPrincipal.getEmail();
		} catch (Exception e) {
			OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
			Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
			email = (String) attributes.get("email");
		}

		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String generateJwtToken(String email) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
	}

	public String generateZendeskJwtToken(User user) {
		var name = String.format("%s.%s", user.getAvatar().getPrefix(), user.getAvatar().getName());
		JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
			.issueTime(new Date())
			.jwtID(UUID.randomUUID().toString())
			.claim("name", name)
			.claim("email", user.getEmail())
			.build();
		
		// Create JWS object
		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaims);
		try {
			// Create HMAC signer
			JWSSigner signer = new MACSigner(ZENDESK_SHARED_SECRET.getBytes());
		  	signedJWT.sign(signer);
		} catch(Exception e) {
		  	System.err.println("Error signing JWT: " + e.getMessage());
		  	return null;
		}

		// Serialise to JWT compact form
		return signedJWT.serialize();
	}
	
	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

//	public String getEmailFromJwtToken(String token) {
//		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().
//	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

}
