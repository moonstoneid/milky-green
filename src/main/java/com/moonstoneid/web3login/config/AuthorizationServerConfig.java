package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.security.Web3AuthenticationConverter;
import com.moonstoneid.web3login.service.KeyPairService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.authentication.AuthenticationConverter;

@Configuration
public class AuthorizationServerConfig {

    private final UserDetailsService userDetailsService;

    public AuthorizationServerConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer() {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> configurer =
                new OAuth2AuthorizationServerConfigurer<>();
        configurer.authorizationEndpoint(authEndpoint -> authEndpoint
                .authorizationRequestConverter(authenticationConverter())
                .consentPage("/oauth2/consent"));
        return configurer;
    }

    public AuthenticationConverter authenticationConverter() {
        Web3AuthenticationConverter converter = new Web3AuthenticationConverter();
        converter.setUserDetailsService(userDetailsService);
        return converter;
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

}
