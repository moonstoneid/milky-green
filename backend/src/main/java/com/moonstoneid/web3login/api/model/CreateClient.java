package com.moonstoneid.web3login.api.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClient {

    private String clientId;
    private String clientSecret;
    private String clientName;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;

    private Boolean isRequireAuthorizationConsent;

    private Long accessTokenValidity;
    private Long refreshTokenValidity;
    private Boolean isReuseRefreshTokens;

}
