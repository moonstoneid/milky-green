package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.oauth2.Web3AuthorizationRequestConverter;
import com.moonstoneid.web3login.service.KeyPairService;
import com.moonstoneid.web3login.service.OidcUserInfoService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer() {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> configurer =
                new OAuth2AuthorizationServerConfigurer<>();
        configurer.authorizationEndpoint(authEndpoint -> authEndpoint
                .authorizationRequestConverter(authorizationRequestConverter())
                .consentPage("/oauth2/consent"));
        return configurer;
    }

    public Web3AuthorizationRequestConverter authorizationRequestConverter() {
        return new Web3AuthorizationRequestConverter();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPairService keyPairService) {
        return ((jwkSelector, securityContext) ->
                jwkSelector.select(new JWKSet(keyPairService.get())));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://localhost:9000").build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
        return (context) -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
                context.getClaims().claims(claims -> claims.putAll(userInfo.getClaims()));
            }
        };
    }

}
