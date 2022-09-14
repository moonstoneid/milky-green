package com.moonstoneid.web3login.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.moonstoneid.web3login.model.Authorization;
import com.moonstoneid.web3login.repo.AuthorizationRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OAuth2AuthorizationService extends BaseOAuth2Service implements
        org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService {

    private final AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthorizationService(AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        authorizationRepository.save(toEntity(authorization));
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return authorizationRepository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        Optional<Authorization> result;
        if (tokenType == null) {
            result = authorizationRepository.findByStateOrAuthorizationCodeOrAccessTokenOrRefreshToken(
                    token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = authorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = authorizationRepository.findByAuthorizationCodeValue(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = authorizationRepository.findByAccessTokenValue(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = authorizationRepository.findByRefreshTokenValue(token);
        } else {
            result = Optional.empty();
        }

        return result.map(this::toObject).orElse(null);
    }

    public List<OAuth2Authorization> getByPrincipalName(String principalName) {
        Assert.hasText(principalName, "principalName cannot be empty");

        List<Authorization> authorizations = authorizationRepository.getAuthorizationsByPrincipalName(
                principalName);

        return authorizations.stream().map(this::toObject).collect(Collectors.toList());
    }

    private OAuth2Authorization toObject(Authorization entity) {
        String registeredClientId = entity.getRegisteredClientId();
        RegisteredClient registeredClient = registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(String.format("The RegisteredClient with ID " +
                    "'%s' was not found.", registeredClientId));
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(
                registeredClient);

        builder.id(entity.getId());
        builder.principalName(entity.getPrincipalName());
        builder.authorizationGrantType(resolveAuthorizationGrantType(
                        entity.getAuthorizationGrantType()));
        builder.attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));

        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(), entity.getAuthorizationCodeIssuedAt(),
                    entity.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(
                    entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            Set<String> accessTokenScopes = StringUtils.commaDelimitedListToSet(
                    entity.getAccessTokenScopes());
            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(), entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(), accessTokenScopes);
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(
                    entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(), entity.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(
                    entity.getRefreshTokenMetadata())));
        }

        if (entity.getIdTokenValue() != null) {
            OidcIdToken idToken = new OidcIdToken(entity.getIdTokenValue(), entity.getIdTokenIssuedAt(),
                    entity.getIdTokenExpiresAt(), parseMap(entity.getIdTokenClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(entity.getIdTokenMetadata())));
        }

        return builder.build();
    }

    private Authorization toEntity(OAuth2Authorization authorization) {
        Authorization entity = new Authorization();

        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAttributes(writeMap(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenValues(authorizationCode, entity::setAuthorizationCodeValue,
                entity::setAuthorizationCodeIssuedAt, entity::setAuthorizationCodeExpiresAt,
                entity::setAuthorizationCodeMetadata);

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(
                OAuth2AccessToken.class);
        setTokenValues(accessToken, entity::setAccessTokenValue, entity::setAccessTokenIssuedAt,
                entity::setAccessTokenExpiresAt, entity::setAccessTokenMetadata);
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(StringUtils.collectionToDelimitedString(
                    accessToken.getToken().getScopes(), ","));
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(
                OAuth2RefreshToken.class);
        setTokenValues(refreshToken, entity::setRefreshTokenValue, entity::setRefreshTokenIssuedAt,
                entity::setRefreshTokenExpiresAt, entity::setRefreshTokenMetadata);

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(
                OidcIdToken.class);
        setTokenValues(oidcIdToken, entity::setIdTokenValue, entity::setIdTokenIssuedAt,
                entity::setIdTokenExpiresAt, entity::setIdTokenMetadata);
        if (oidcIdToken != null && oidcIdToken.getClaims() != null) {
            entity.setIdTokenClaims(writeMap(oidcIdToken.getClaims()));
        }

        return entity;
    }

    private void setTokenValues(OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer, Consumer<Instant> issuedAtConsumer,
            Consumer<Instant> expiresAtConsumer, Consumer<String> metadataConsumer) {
        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
            expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }
    }

}
