package com.moonstoneid.web3login.service;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonstoneid.web3login.security.Web3SecurityJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

public abstract class BaseOAuth2Service {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BaseOAuth2Service() {
        ClassLoader classLoader = BaseOAuth2Service.class.getClassLoader();

        objectMapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new Web3SecurityJackson2Module());
    }

    protected Map<String, Object> parseMap(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    protected String writeMap(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    protected static AuthorizationGrantType resolveAuthorizationGrantType(
            String authorizationGrantType) {
        if (Objects.equals(authorizationGrantType,
                AuthorizationGrantType.AUTHORIZATION_CODE.getValue())) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (Objects.equals(authorizationGrantType,
                AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (Objects.equals(authorizationGrantType,
                AuthorizationGrantType.REFRESH_TOKEN.getValue())) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else {
            return new AuthorizationGrantType(authorizationGrantType);
        }
    }

    protected static ClientAuthenticationMethod resolveClientAuthenticationMethod(
            String clientAuthenticationMethod) {
        if (Objects.equals(clientAuthenticationMethod,
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue())) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (Objects.equals(clientAuthenticationMethod,
                ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue())) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (Objects.equals(clientAuthenticationMethod,
                ClientAuthenticationMethod.NONE.getValue())) {
            return ClientAuthenticationMethod.NONE;
        } else {
            return new ClientAuthenticationMethod(clientAuthenticationMethod);
        }
    }

}
