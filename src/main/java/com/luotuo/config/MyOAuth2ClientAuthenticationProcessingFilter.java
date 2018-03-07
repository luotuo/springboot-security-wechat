package com.luotuo.config;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by luotuo on 17-9-27.
 */
public class MyOAuth2ClientAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    public OAuth2RestOperations restTemplate;
    private MyResourceServerTokenServices tokenServices;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();
    private ApplicationEventPublisher eventPublisher;

    public void setTokenServices(MyResourceServerTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    public void setRestTemplate(OAuth2RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        super.setApplicationEventPublisher(eventPublisher);
    }

    public MyOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        this.setAuthenticationManager(new MyOAuth2ClientAuthenticationProcessingFilter.NoopAuthenticationManager());
        this.setAuthenticationDetailsSource(this.authenticationDetailsSource);
    }

    public void afterPropertiesSet() {
        Assert.state(this.restTemplate != null, "Supply a rest-template");
        super.afterPropertiesSet();
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        OAuth2AccessToken accessToken;
        BadCredentialsException bad;
        try {
            accessToken = this.restTemplate.getAccessToken();
        } catch (OAuth2Exception var7) {
            bad = new BadCredentialsException("Could not obtain access token", var7);
            this.publish(new OAuth2AuthenticationFailureEvent(bad));
            throw bad;
        }

        try {
            OAuth2Authentication result = this.tokenServices.loadAuthentication(accessToken.getValue(),
                    request.getRemoteHost());
            if(this.authenticationDetailsSource != null) {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, accessToken.getValue());
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, accessToken.getTokenType());
                result.setDetails(this.authenticationDetailsSource.buildDetails(request));
            }

            this.publish(new AuthenticationSuccessEvent(result));
            return result;
        } catch (InvalidTokenException var6) {
            bad = new BadCredentialsException("Could not obtain user details from token", var6);
            this.publish(new OAuth2AuthenticationFailureEvent(bad));
            throw bad;
        }
    }

    private void publish(ApplicationEvent event) {
        if(this.eventPublisher != null) {
            this.eventPublisher.publishEvent(event);
        }

    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        this.restTemplate.getAccessToken();
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if(failed instanceof AccessTokenRequiredException) {
            throw failed;
        } else {
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {
        private NoopAuthenticationManager() {
        }

        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }
    }
}

