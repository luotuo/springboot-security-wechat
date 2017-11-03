package com.jwcq.config;

import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.filter.state.DefaultStateKeyGenerator;
import org.springframework.security.oauth2.client.filter.state.StateKeyGenerator;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.*;
import org.springframework.security.oauth2.client.token.auth.ClientAuthenticationHandler;
import org.springframework.security.oauth2.client.token.auth.DefaultClientAuthenticationHandler;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Created by luotuo on 17-9-27.
 */
public class MyAuthorizationCodeAccessTokenProvider extends OAuth2AccessTokenSupport implements AccessTokenProvider {
    private StateKeyGenerator stateKeyGenerator = new DefaultStateKeyGenerator();
    private String scopePrefix = "scope.";
    private RequestEnhancer authorizationRequestEnhancer = new DefaultRequestEnhancer();
    private boolean stateMandatory = true;
    MappingJackson2HttpMessageConverter customJsonMessageConverter = new
            MappingJackson2HttpMessageConverter();

    public MyAuthorizationCodeAccessTokenProvider() {
        customJsonMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
        this.setMessageConverters(Arrays.asList(customJsonMessageConverter));
    }

    public void setStateMandatory(boolean stateMandatory) {
        this.stateMandatory = stateMandatory;
    }

    public void setAuthorizationRequestEnhancer(RequestEnhancer authorizationRequestEnhancer) {
        this.authorizationRequestEnhancer = authorizationRequestEnhancer;
    }

    public void setScopePrefix(String scopePrefix) {
        this.scopePrefix = scopePrefix;
    }

    public void setStateKeyGenerator(StateKeyGenerator stateKeyGenerator) {
        this.stateKeyGenerator = stateKeyGenerator;
    }

    public boolean supportsResource(OAuth2ProtectedResourceDetails resource) {
        return resource instanceof AuthorizationCodeResourceDetails && "authorization_code".equals(resource.getGrantType());
    }

    public boolean supportsRefresh(OAuth2ProtectedResourceDetails resource) {
        return this.supportsResource(resource);
    }

    public String obtainAuthorizationCode(OAuth2ProtectedResourceDetails details, final AccessTokenRequest request) throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException, OAuth2AccessDeniedException {
        AuthorizationCodeResourceDetails resource = (AuthorizationCodeResourceDetails)details;
        HttpHeaders headers = this.getHeadersForAuthorizationRequest(request);
        MultiValueMap<String, String> form = new LinkedMultiValueMap();
        if(request.containsKey("user_oauth_approval")) {
            form.set("user_oauth_approval", request.getFirst("user_oauth_approval"));
            Iterator var6 = details.getScope().iterator();

            while(var6.hasNext()) {
                String scope = (String)var6.next();
                form.set(this.scopePrefix + scope, request.getFirst("user_oauth_approval"));
            }
        } else {
            form.putAll(this.getParametersForAuthorizeRequest(resource, request));
        }

        this.authorizationRequestEnhancer.enhance(request, resource, form, headers);
        final ResponseExtractor<ResponseEntity<Void>> delegate = this.getAuthorizationResponseExtractor();
        ResponseExtractor<ResponseEntity<Void>> extractor = new ResponseExtractor<ResponseEntity<Void>>() {
            public ResponseEntity<Void> extractData(ClientHttpResponse response) throws IOException {
                if(response.getHeaders().containsKey("Set-Cookie")) {
                    request.setCookie(response.getHeaders().getFirst("Set-Cookie"));
                }

                return (ResponseEntity)delegate.extractData(response);
            }
        };
        ResponseEntity<Void> response = (ResponseEntity)this.getRestTemplate().execute(resource.getUserAuthorizationUri(), HttpMethod.POST, this.getRequestCallback(resource, form, headers), extractor, form.toSingleValueMap());
        if(response.getStatusCode() == HttpStatus.OK) {
            throw this.getUserApprovalSignal(resource, request);
        } else {
            URI location = response.getHeaders().getLocation();
            String query = location.getQuery();
            Map<String, String> map = OAuth2Utils.extractMap(query);
            String redirectUri;
            if(map.containsKey("state")) {
                request.setStateKey((String)map.get("state"));
                if(request.getPreservedState() == null) {
                    redirectUri = resource.getRedirectUri(request);
                    if(redirectUri != null) {
                        request.setPreservedState(redirectUri);
                    } else {
                        request.setPreservedState(new Object());
                    }
                }
            }

            redirectUri = (String)map.get("code");
            if(redirectUri == null) {
                throw new UserRedirectRequiredException(location.toString(), form.toSingleValueMap());
            } else {
                request.set("code", redirectUri);
                return redirectUri;
            }
        }
    }

    protected ResponseExtractor<ResponseEntity<Void>> getAuthorizationResponseExtractor() {
        return new ResponseExtractor<ResponseEntity<Void>>() {
            public ResponseEntity<Void> extractData(ClientHttpResponse response) throws IOException {
                return new ResponseEntity(response.getHeaders(), response.getStatusCode());
            }
        };
    }

    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request) throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException, OAuth2AccessDeniedException {
        AuthorizationCodeResourceDetails resource = (AuthorizationCodeResourceDetails)details;
        System.out.println(request.getCurrentUri());
        if(request.getAuthorizationCode() == null) {
            if(request.getStateKey() == null) {
                throw this.getRedirectForAuthorization(resource, request);
            }

            this.obtainAuthorizationCode(resource, request);
        }
        System.out.println("code == " + request.getAuthorizationCode());
        return this.retrieveToken(request,
                resource, this.getParametersForTokenRequest(resource, request), this.getHeadersForTokenRequest(request));
    }

    public OAuth2AccessToken refreshAccessToken(OAuth2ProtectedResourceDetails resource, OAuth2RefreshToken refreshToken, AccessTokenRequest request) throws UserRedirectRequiredException, OAuth2AccessDeniedException {
        MultiValueMap<String, String> form = new LinkedMultiValueMap();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken.getValue());
        form.add("appid", resource.getClientId());

        try {
            return this.retrieveToken(request, resource, form, this.getHeadersForTokenRequest(request));
        } catch (OAuth2AccessDeniedException var6) {
            throw this.getRedirectForAuthorization((AuthorizationCodeResourceDetails)resource, request);
        }
    }
    private ClientAuthenticationHandler authenticationHandler = new DefaultClientAuthenticationHandler();
    private RequestEnhancer tokenRequestEnhancer = new DefaultRequestEnhancer();

    protected OAuth2AccessToken retrieveToken(final AccessTokenRequest request,
                                              OAuth2ProtectedResourceDetails resource,
                                              MultiValueMap<String, String> form,
                                              HttpHeaders headers) throws OAuth2AccessDeniedException {
        try {
            this.authenticationHandler.authenticateTokenRequest(resource, form, headers);
            this.tokenRequestEnhancer.enhance(request, resource, form, headers);
            final ResponseExtractor<OAuth2AccessToken> delegate = this.getResponseExtractor();

            ResponseExtractor<OAuth2AccessToken> extractor = new ResponseExtractor<OAuth2AccessToken>() {
                public OAuth2AccessToken extractData(ClientHttpResponse response) throws IOException {
                    if(response.getHeaders().containsKey("Set-Cookie")) {
                        request.setCookie(response.getHeaders().getFirst("Set-Cookie"));
                    }

                    return (OAuth2AccessToken)delegate.extractData(response);
                }
            };
            System.out.println("URI == " + this.getAccessTokenUri(resource, form));
            return (OAuth2AccessToken)this.getRestTemplate().execute(this.getAccessTokenUri(resource, form),
                    this.getHttpMethod(),
                    this.getRequestCallback(resource, form, headers),
                    extractor,
                    form.toSingleValueMap());
        } catch (OAuth2Exception var8) {
            System.out.println(var8.toString());
            throw new OAuth2AccessDeniedException("Access token denied.", resource, var8);
        } catch (RestClientException var9) {
            System.out.println(var9.toString());
            throw new OAuth2AccessDeniedException("Error requesting access token.", resource, var9);
        }
    }

    protected HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    protected String getAccessTokenUri(OAuth2ProtectedResourceDetails resource, MultiValueMap<String, String> form) {
        String accessTokenUri = resource.getAccessTokenUri();
        if (form.containsKey("refresh_token"))
            accessTokenUri = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Retrieving token from " + accessTokenUri);
        }

        StringBuilder builder = new StringBuilder(accessTokenUri);
        if(this.getHttpMethod() == HttpMethod.GET) {
            String separator = "?";
            if(accessTokenUri.contains("?")) {
                separator = "&";
            }

            for(Iterator var6 = form.keySet().iterator(); var6.hasNext(); separator = "&") {
                String key = (String)var6.next();
                builder.append(separator);
                builder.append(key + "={" + key + "}");
            }
        }

        if (form.containsKey("refresh_token"))
            return builder.toString();
        return builder.toString() + "#wechat_redirect";
    }

    private HttpHeaders getHeadersForTokenRequest(AccessTokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    private HttpHeaders getHeadersForAuthorizationRequest(AccessTokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        if(request.getCookie() != null) {
            headers.set("Cookie", request.getCookie());
        }

        return headers;
    }

    private MultiValueMap<String, String> getParametersForTokenRequest(AuthorizationCodeResourceDetails resource, AccessTokenRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap();
        String state = request.getStateKey();
//        if (state.contains("session")) {
//            form.set("appid", resource.getClientId());
//            form.set("secret", resource.getClientSecret());
//        } else {
//            form.set("appid", "wx38871ac04c8208af");
//            form.set("secret", "50f7e835165d91006bf32fb3ba8d53dd");
//        }
        form.set("appid", resource.getClientId());
        form.set("secret", resource.getClientSecret());
        form.set("code", request.getAuthorizationCode());
        form.set("grant_type", "authorization_code");
        Object preservedState = request.getPreservedState();
        //if((request.getStateKey() != null || this.stateMandatory) && preservedState == null) {
        if(false) {
            throw new InvalidRequestException("Possible CSRF detected - state parameter was required but no state could be found");
        } else {
            String redirectUri = null;
            if(preservedState instanceof String) {
                redirectUri = String.valueOf(preservedState);
            } else {
                redirectUri = resource.getRedirectUri(request);
            }

            if(redirectUri != null && !"NONE".equals(redirectUri)) {
                form.set("redirect_uri", redirectUri);
            }

            return form;
        }
    }

    private MultiValueMap<String, String> getParametersForAuthorizeRequest(AuthorizationCodeResourceDetails resource, AccessTokenRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap();
        form.set("response_type", "code");
        form.set("client_id", resource.getClientId());
        if(request.get("scope") != null) {
            form.set("scope", request.getFirst("scope"));
        } else {
            form.set("scope", OAuth2Utils.formatParameterList(resource.getScope()));
        }

        String redirectUri = resource.getPreEstablishedRedirectUri();
        Object preservedState = request.getPreservedState();
        if(redirectUri == null && preservedState != null) {
            redirectUri = String.valueOf(preservedState);
        } else {
            redirectUri = request.getCurrentUri();
        }

        String stateKey = request.getStateKey();
        if(stateKey != null) {
            form.set("state", stateKey);
            if(preservedState == null) {
                throw new InvalidRequestException("Possible CSRF detected - state parameter was present but no state could be found");
            }
        }

        if(redirectUri != null) {
            form.set("redirect_uri", redirectUri);
        }

        return form;
    }

    private UserRedirectRequiredException getRedirectForAuthorization(AuthorizationCodeResourceDetails resource, AccessTokenRequest request) {
        TreeMap<String, String> requestParameters = new TreeMap();
        requestParameters.put("response_type", "code");
        requestParameters.put("client_id", resource.getClientId());
        String redirectUri = resource.getRedirectUri(request);
        if(redirectUri != null) {
            requestParameters.put("redirect_uri", redirectUri);
        }

        if(resource.isScoped()) {
            StringBuilder builder = new StringBuilder();
            List<String> scope = resource.getScope();
            if(scope != null) {
                Iterator scopeIt = scope.iterator();

                while(scopeIt.hasNext()) {
                    builder.append((String)scopeIt.next());
                    if(scopeIt.hasNext()) {
                        builder.append(' ');
                    }
                }
            }

            requestParameters.put("scope", builder.toString());
        }

        UserRedirectRequiredException redirectException = new UserRedirectRequiredException(resource.getUserAuthorizationUri(), requestParameters);
        String stateKey = this.stateKeyGenerator.generateKey(resource);
        redirectException.setStateKey(stateKey);
        request.setStateKey(stateKey);
        redirectException.setStateToPreserve(redirectUri);
        request.setPreservedState(redirectUri);
        return redirectException;
    }

    protected UserApprovalRequiredException getUserApprovalSignal(AuthorizationCodeResourceDetails resource, AccessTokenRequest request) {
        String message = String.format("Do you approve the client '%s' to access your resources with scope=%s", new Object[]{resource.getClientId(), resource.getScope()});
        return new UserApprovalRequiredException(resource.getUserAuthorizationUri(), Collections.singletonMap("user_oauth_approval", message), resource.getClientId(), resource.getScope());
    }
}