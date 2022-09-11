package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.repo.AuthorizationConsentRepository;
import com.moonstoneid.web3login.repo.AuthorizationRepository;
import com.moonstoneid.web3login.repo.ClientRepository;
import com.moonstoneid.web3login.security.Web3AuthenticationConverter;
import com.moonstoneid.web3login.service.JpaAuthorizationConsentService;
import com.moonstoneid.web3login.service.JpaAuthorizationService;
import com.moonstoneid.web3login.service.JpaRegisteredClientRepository;
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
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://localhost:9000").build();
    }

}
