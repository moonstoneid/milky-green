package org.mn.web3login.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.mn.web3login.jose.Jwks;
import org.mn.web3login.repo.AuthorizationConsentRepository;
import org.mn.web3login.repo.AuthorizationRepository;
import org.mn.web3login.repo.ClientRepository;
import org.mn.web3login.security.Web3AuthenticationConverter;
import org.mn.web3login.service.JpaAuthorizationConsentService;
import org.mn.web3login.service.JpaAuthorizationService;
import org.mn.web3login.service.JpaRegisteredClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    @Primary
    public JpaRegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        return new JpaRegisteredClientRepository(clientRepository);
    }

    @Bean
    @Primary
    public JpaAuthorizationService authorizationService(
            AuthorizationRepository authorizationRepository,
            JpaRegisteredClientRepository registeredClientRepository) {
        return new JpaAuthorizationService(authorizationRepository, registeredClientRepository);
    }

    @Bean
    @Primary
    public JpaAuthorizationConsentService authorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            JpaRegisteredClientRepository registeredClientRepository) {
        return new JpaAuthorizationConsentService(authorizationConsentRepository,
                registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://localhost:9000").build();
    }

}
