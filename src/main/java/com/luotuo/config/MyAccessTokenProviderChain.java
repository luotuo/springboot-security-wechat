package com.luotuo.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luotuo on 17-9-27.
 */
public class MyAccessTokenProviderChain extends OAuth2AccessTokenSupport implements AccessTokenProvider {
    private final List<AccessTokenProvider> chain;
    private ClientTokenServices clientTokenServices;

    public MyAccessTokenProviderChain(List<? extends AccessTokenProvider> chain) {
        this.chain = chain == null? Collections.emptyList():Collections.unmodifiableList(chain);
    }

    public void setClientTokenServices(ClientTokenServices clientTokenServices) {
        this.clientTokenServices = clientTokenServices;
    }

    public boolean supportsResource(OAuth2ProtectedResourceDetails resource) {
        Iterator var2 = this.chain.iterator();

        AccessTokenProvider tokenProvider;
        do {
            if(!var2.hasNext()) {
                return false;
            }

            tokenProvider = (AccessTokenProvider)var2.next();
        } while(!tokenProvider.supportsResource(resource));

        return true;
    }

    public boolean supportsRefresh(OAuth2ProtectedResourceDetails resource) {
        Iterator var2 = this.chain.iterator();

        AccessTokenProvider tokenProvider;
        do {
            if(!var2.hasNext()) {
                return false;
            }

            tokenProvider = (AccessTokenProvider)var2.next();
        } while(!tokenProvider.supportsRefresh(resource));

        return true;
    }

    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails resource, AccessTokenRequest request) throws UserRedirectRequiredException, AccessDeniedException {
        OAuth2AccessToken accessToken = null;
        OAuth2AccessToken existingToken = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof AnonymousAuthenticationToken && !resource.isClientOnly()) {
            throw new InsufficientAuthenticationException("Authentication is required to obtain an access token (anonymous not allowed)");
        } else {
            if(resource.isClientOnly() || auth != null && auth.isAuthenticated()) {
                existingToken = request.getExistingToken();
                if(existingToken == null && this.clientTokenServices != null) {
                    existingToken = this.clientTokenServices.getAccessToken(resource, auth);
                }

                if(existingToken != null) {
                    if(existingToken.isExpired()) {
                        if(this.clientTokenServices != null) {
                            this.clientTokenServices.removeAccessToken(resource, auth);
                        }

                        OAuth2RefreshToken refreshToken = existingToken.getRefreshToken();
                        if(refreshToken != null) {
                            accessToken = this.refreshAccessToken(resource, refreshToken, request);
                        }
                    } else {
                        accessToken = existingToken;
                    }
                }
            }

            if(accessToken == null) {
                accessToken = this.obtainNewAccessTokenInternal(resource, request);
                if(accessToken == null) {
                    System.out.println("An OAuth 2 access token must be obtained or an exception thrown.");
                    throw new IllegalStateException("An OAuth 2 access token must be obtained or an exception thrown.");
                }
            }

            if(this.clientTokenServices != null && (resource.isClientOnly() || auth != null && auth.isAuthenticated())) {
                this.clientTokenServices.saveAccessToken(resource, auth, accessToken);
            }

            return accessToken;
        }
    }

    protected OAuth2AccessToken obtainNewAccessTokenInternal(OAuth2ProtectedResourceDetails details, AccessTokenRequest request) throws UserRedirectRequiredException, AccessDeniedException {
        if(request.isError()) {
            throw OAuth2Exception.valueOf(request.toSingleValueMap());
        } else {
            Iterator var3 = this.chain.iterator();

            AccessTokenProvider tokenProvider;
            do {
                if(!var3.hasNext()) {
                    throw new OAuth2AccessDeniedException("Unable to obtain a new access token for resource '" + details.getId() + "'. The provider manager is not configured to support it.", details);
                }

                tokenProvider = (AccessTokenProvider)var3.next();
            } while(!tokenProvider.supportsResource(details));
            if (tokenProvider != null)
                System.out.println("tokeProvider == " + tokenProvider.toString());
            return tokenProvider.obtainAccessToken(details, request);
        }
    }

    public OAuth2AccessToken refreshAccessToken(OAuth2ProtectedResourceDetails resource, OAuth2RefreshToken refreshToken, AccessTokenRequest request) throws UserRedirectRequiredException {
        Iterator var4 = this.chain.iterator();

        AccessTokenProvider tokenProvider;
        do {
            if(!var4.hasNext()) {
                throw new OAuth2AccessDeniedException("Unable to obtain a new access token for resource '" + resource.getId() + "'. The provider manager is not configured to support it.", resource);
            }

            tokenProvider = (AccessTokenProvider)var4.next();
        } while(!tokenProvider.supportsRefresh(resource));

        DefaultOAuth2AccessToken refreshedAccessToken = new DefaultOAuth2AccessToken(tokenProvider.refreshAccessToken(resource, refreshToken, request));
        if(refreshedAccessToken.getRefreshToken() == null) {
            refreshedAccessToken.setRefreshToken(refreshToken);
        }

        return refreshedAccessToken;
    }
}
