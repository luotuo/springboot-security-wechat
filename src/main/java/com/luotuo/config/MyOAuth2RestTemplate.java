package com.luotuo.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by luotuo on 17-9-27.
 */
public class MyOAuth2RestTemplate extends OAuth2RestTemplate {
    private final OAuth2ProtectedResourceDetails resource;
    private AccessTokenProvider accessTokenProvider;
    private OAuth2ClientContext context;
    private boolean retryBadAccessTokens;
    private OAuth2RequestAuthenticator authenticator;

    public MyOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource) {
        this(resource, new DefaultOAuth2ClientContext());
    }

    public MyOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        super(resource, context);
        this.accessTokenProvider = new MyAccessTokenProviderChain(Arrays.asList(new AccessTokenProvider[]{new MyAuthorizationCodeAccessTokenProvider(),
                new ImplicitAccessTokenProvider(),
                new ResourceOwnerPasswordAccessTokenProvider(),
                new ClientCredentialsAccessTokenProvider()}));
        this.retryBadAccessTokens = true;
        this.authenticator = new DefaultOAuth2RequestAuthenticator();
        if(resource == null) {
            throw new IllegalArgumentException("An OAuth2 resource must be supplied.");
        } else {
            this.resource = resource;
            this.context = context;
            this.setErrorHandler(new OAuth2ErrorHandler(resource));
        }
    }

    public void setAuthenticator(OAuth2RequestAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setRetryBadAccessTokens(boolean retryBadAccessTokens) {
        this.retryBadAccessTokens = retryBadAccessTokens;
    }

    public OAuth2ProtectedResourceDetails getResource() {
        return this.resource;
    }

    private String getClientId() {
        return this.resource.getClientId();
    }

    public OAuth2AccessToken getAccessToken() throws UserRedirectRequiredException {
        OAuth2AccessToken accessToken = this.context.getAccessToken();
        if(accessToken == null || accessToken.isExpired()) {
            try {
                accessToken = this.acquireAccessToken(this.context);
            } catch (UserRedirectRequiredException var5) {
                this.context.setAccessToken((OAuth2AccessToken)null);
                accessToken = null;
                String stateKey = var5.getStateKey();
                if(stateKey != null) {
                    Object stateToPreserve = var5.getStateToPreserve();
                    if(stateToPreserve == null) {
                        stateToPreserve = "NONE";
                    }

                    this.context.setPreservedState(stateKey, stateToPreserve);
                }

                throw var5;
            }
        }

        return accessToken;
    }

    public OAuth2ClientContext getOAuth2ClientContext() {
        return this.context;
    }

    protected OAuth2AccessToken acquireAccessToken(OAuth2ClientContext oauth2Context) throws UserRedirectRequiredException {
        AccessTokenRequest accessTokenRequest = oauth2Context.getAccessTokenRequest();
        if (accessTokenRequest != null) {
            System.out.println("accesstokeRequest == " + accessTokenRequest.getCurrentUri());
        }
        if(accessTokenRequest == null) {
            throw new AccessTokenRequiredException("No OAuth 2 security context has been established. Unable to access resource '" + this.resource.getId() + "'.", this.resource);
        } else {
            String stateKey = accessTokenRequest.getStateKey();
            if(stateKey != null) {
                System.out.println("stateKey == " + stateKey);
                accessTokenRequest.setPreservedState(oauth2Context.removePreservedState(stateKey));
            }

            OAuth2AccessToken existingToken = oauth2Context.getAccessToken();
            if(existingToken != null) {
                accessTokenRequest.setExistingToken(existingToken);
            }

            OAuth2AccessToken accessToken = null;
            accessToken = this.accessTokenProvider.obtainAccessToken(this.resource, accessTokenRequest);
            if(accessToken != null && accessToken.getValue() != null) {
                oauth2Context.setAccessToken(accessToken);
                return accessToken;
            } else {
                throw new IllegalStateException("Access token provider returned a null access token, which is illegal according to the contract.");
            }
        }
    }

    @Override
    protected URI appendQueryParameter(URI uri, OAuth2AccessToken accessToken) {
        try {
            String query = uri.getRawQuery();
            String queryFragment = this.resource.getTokenName() + "=" + URLEncoder.encode(accessToken.getValue(), "UTF-8");
            if(query == null) {
                query = queryFragment;
            } else {
                query = query + "&" + queryFragment;
            }
            String openid = (String) accessToken
                    .getAdditionalInformation().get("openid");
            System.out.println("openid == " + openid);
            String openIdQueryFragment = "openid=" + URLEncoder.encode(openid, "UTF-8");
            query = query + "&" + openIdQueryFragment;
            URI update = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), (String)null, (String)null);
            StringBuffer sb = new StringBuffer(update.toString());
            sb.append("?");
            sb.append(query);
            if(uri.getFragment() != null) {
                sb.append("#");
                sb.append(uri.getFragment());
            }
            System.out.println("appendQueryParameter == " + sb.toString());
            return new URI(sb.toString());
        } catch (URISyntaxException var7) {
            throw new IllegalArgumentException("Could not parse URI", var7);
        } catch (UnsupportedEncodingException var8) {
            throw new IllegalArgumentException("Could not encode URI", var8);
        }
    }
}

