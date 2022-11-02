package com.moonstoneid.web3login.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.moonstoneid.web3login.model.AuthorizationConsent;
import com.moonstoneid.web3login.repo.AuthorizationConsentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Primary
public class OAuth2AuthorizationConsentService extends BaseOAuth2Service implements
        org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService {

    private final AuthorizationConsentRepository authorizationConsentRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        this.authorizationConsentRepository = authorizationConsentRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        authorizationConsentRepository.save(toEntity(authorizationConsent));
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        authorizationConsentRepository.deleteById(new AuthorizationConsent.AuthorizationConsentId(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName()));
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        return authorizationConsentRepository.findByClientIdAndUsername(registeredClientId,
                principalName).map(this::toObject).orElse(null);
    }

    public List<OAuth2AuthorizationConsent> getByPrincipalName(String principalName) {
        Assert.hasText(principalName, "principalName cannot be empty");

        List<AuthorizationConsent> authorizationConsents = authorizationConsentRepository
                .getAuthorizationConsentsByUsername(principalName);

        return authorizationConsents.stream().map(this::toObject).collect(Collectors.toList());
    }

    private OAuth2AuthorizationConsent toObject(AuthorizationConsent authorizationConsent) {
        String clientId = authorizationConsent.getClientId();
        RegisteredClient registeredClient = registeredClientRepository.findById(clientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(String.format("The RegisteredClient with ID " +
                    "'%s' was not found.", clientId));
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                clientId, authorizationConsent.getUsername());

        if (authorizationConsent.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(
                    authorizationConsent.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }

        return builder.build();
    }

    private AuthorizationConsent toEntity(OAuth2AuthorizationConsent authorizationConsent) {
        AuthorizationConsent entity = new AuthorizationConsent();

        entity.setClientId(authorizationConsent.getRegisteredClientId());
        entity.setUsername(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        entity.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));

        return entity;
    }

}
