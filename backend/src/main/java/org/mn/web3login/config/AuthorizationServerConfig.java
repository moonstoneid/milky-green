package org.mn.web3login.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.mn.web3login.jose.Jwks;
import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.util.ParamUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer() {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authServConfig = new OAuth2AuthorizationServerConfigurer<>();
        authServConfig.authorizationEndpoint(authEndpoint ->
                authEndpoint
                        .authorizationRequestConverter(createCustomAuthRequestConverter())
                        .consentPage("/oauth2/consent"));
        return authServConfig;
    }

    private AuthenticationConverter createCustomAuthRequestConverter() {
        final OAuth2AuthorizationCodeRequestAuthenticationConverter delegate =
                new OAuth2AuthorizationCodeRequestAuthenticationConverter();
        return (request) -> {
            OAuth2AuthorizationCodeRequestAuthenticationToken authCodeAuthentication =
                    (OAuth2AuthorizationCodeRequestAuthenticationToken) delegate.convert(request);

            // Check signature for POST /oauth2/authorize requests, not for GET /oauth2/authorize requests
            if ("POST".equals(request.getMethod())){
                authenticate(request);
            }

            return authCodeAuthentication;
        };
    }

    private void authenticate(HttpServletRequest request){
        String[] credentials = ParamUtil.parseCredentialsFromRequest(request);
        String message = credentials[0];
        String signature = credentials[1];
        String nonce = credentials[2];

        SiweMessage siweMessage = null;
        UserDetails userDetails = null;
        String address = null;

        try {
            // Try to parse the String. Throws an exception if message is not a valid EIP-4361 message.
            siweMessage = new SiweMessage(message);
            address = siweMessage.getMAddress();

            // Throws UsernameNotFoundException
            userDetails = userDetailsService.loadUserByUsername(address);

            // Validate signature. Throws an exception if signature is invalid,
            // mandatory fields are missing, expiration has been reached or now < notBefore
            siweMessage.validate(signature);

        } catch (SiweException e) {
            switch (e.getMErrorType()) {
                case INVALID_SIGNATURE:
                    throwError("invalid_request", "Invalid signature");
                case EXPIRED_MESSAGE:
                    throwError("invalid_request", "Message expired");
                case MALFORMED_SESSION:
                    throwError("invalid_request", "Malformed session");
                case MALFORMED_MESSAGE:
                    throwError("invalid_request", "Malformed message");
                case NOTBEFORE_MESSAGE:
                    throwError("invalid_request", "Message not yet valid");
                default:
                    throwError("invalid_request", "Unknown error ");
            }
        }

        // Check nonce
        if(!nonce.equals(siweMessage.getMNonce())){
            throwError("invalid_request", "Incorrect nonce.");
        }
    }

    private static void throwError(String errorCode, String parameterName) {
        throwError(errorCode, parameterName, "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1");
    }

    private static void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, (OAuth2AuthorizationCodeRequestAuthenticationToken) null);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("test-client")
                .clientSecret("{noop}test-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope("openid")
                .scope("message.read")
                .scope("message.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);
        repository.save(client);

        return repository;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
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

    @Bean
    public EmbeddedDatabase embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .build();
    }
    
}
