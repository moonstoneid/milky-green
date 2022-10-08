package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.repo.AuthorizationConsentRepository;
import com.moonstoneid.web3login.repo.AuthorizationRepository;
import com.moonstoneid.web3login.repo.ClientRepository;
import com.moonstoneid.web3login.repo.KeyPairRepository;
import com.moonstoneid.web3login.security.Web3AuthenticationConverter;
import com.moonstoneid.web3login.service.*;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
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
    @Primary
    public RegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        return new RegisteredClientRepository(clientRepository);
    }

    @Bean
    @Primary
    public OAuth2AuthorizationService authorizationService(
            AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
    }

    @Bean
    @Primary
    public OAuth2AuthorizationConsentService authorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new OAuth2AuthorizationConsentService(authorizationConsentRepository,
                registeredClientRepository);
    }

    @Bean
    public KeyPairService keyPairService(KeyPairRepository keyPairRepository) {
        return new KeyPairService(keyPairRepository);
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
