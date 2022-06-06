package com.ndb.auction.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ndb.auction.models.LocationLog;
import com.ndb.auction.service.LocationLogService;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.UserDetailsServiceImpl;
import com.ndb.auction.utils.RemoteIpHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private LocationLogService locationLogService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private static final String SESSION_IP = "ip";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        UserDetailsImpl userDetails = null;
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        block_ipcheck:
        {
            HttpSession session = request.getSession(false);
            String ipFromSession;
            String ip = RemoteIpHelper.getRemoteIpFrom(request);
            if (session == null || (ipFromSession = (String) session.getAttribute(SESSION_IP)) == null
                    || !ip.equals(ipFromSession)) {
                LocationLog location = locationLogService.buildLog(ip);
                JsonObject errorObject;
                if (location == null) {
                    if (userDetails != null) {
                        location = new LocationLog();
                        location.setUserId(userDetails.getId());
                        location.setIpAddress(ip);
                        locationLogService.addLog(location);
                        if (session == null)
                            session = request.getSession(true);
                        session.setAttribute(SESSION_IP, ip);
                    }
                    break block_ipcheck;
                } else {
                    if (userDetails != null) {
                        location.setUserId(userDetails.getId());
                        locationLogService.addLog(location);
                        if (session == null)
                            session = request.getSession(true);
                        session.setAttribute(SESSION_IP, ip);
                    }
                    if (locationLogService.isProxyOrVPN(location)) {
                        String message = "anonymous proxy or VPN";
                        location.setFinalResult(message);
                        errorObject = new JsonObject();
                        errorObject.addProperty("isAnonymousIp", true);
                        errorObject.addProperty("message", message);
                    } else if (!locationLogService.isAllowedCountry(location.getCountryCode())) {
                        String message = "banned country";
                        location.setFinalResult(message);
                        errorObject = new JsonObject();
                        errorObject.addProperty("isBannedCountry", true);
                        errorObject.addProperty("country", location.getCountry());
                        errorObject.addProperty("countryCode", location.getCountryCode());
                        errorObject.addProperty("message", message);
                    } else {
                        break block_ipcheck;
                    }
                }
                JsonArray errorsArray = new JsonArray();
                errorsArray.add(errorObject);
                JsonObject responseObject = new JsonObject();
                responseObject.add("errors", errorsArray);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(responseObject.toString());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher();
        return 
            matcher.match("/location", request.getServletPath()) || 
            matcher.match("/favicon.ico", request.getServletPath()) ||
            matcher.match("/shufti", request.getServletPath()) ||
            matcher.match("/stripe", request.getServletPath()) ||
            matcher.match("/ipn/**", request.getServletPath()) ||
            matcher.match("/paypal/**", request.getServletPath());
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
