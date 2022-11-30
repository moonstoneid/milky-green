package com.moonstoneid.milkygreen.api.model;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateClient")
public class CreateClientAM {

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
