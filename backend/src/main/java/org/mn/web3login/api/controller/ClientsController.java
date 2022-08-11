package org.mn.web3login.api.controller;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.mn.web3login.AppConstants;
import org.mn.web3login.api.model.Client;
import org.mn.web3login.api.doc.ClientsApi;
import org.mn.web3login.api.model.CreateClient;
import org.mn.web3login.api.model.UpdateClient;
import org.mn.web3login.service.JpaRegisteredClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class ClientsController implements ClientsApi {

    private static final String CLIENT_SECRET_PREFIX = "{noop}";
    private static final int CLIENT_SECRET_DEFAULT_LENGTH = 32;

    private final JpaRegisteredClientRepository registeredClientRepository;

    public ClientsController(JpaRegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    @GetMapping(value = "/clients", produces = { "application/json" })
    public @ResponseBody List<Client> getClients() {
        List<RegisteredClient> clients = registeredClientRepository.getAll();
        return toApiModel(clients);
    }

    @Override
    @PostMapping(value = "/clients", produces = { "application/json" })
    public @ResponseBody Client createClient(@RequestBody CreateClient apiCreateClient) {
        validateCreateClientRequest(apiCreateClient);

        checkClientIdIsNotTaken(null, apiCreateClient.getClientId());

        String id = generateId();
        String clientSecret = apiCreateClient.getClientSecret();
        if (clientSecret == null) {
            clientSecret = generateClientSecret();
        }

        RegisteredClient client = toModel(id, apiCreateClient, clientSecret);

        registeredClientRepository.save(client);

        return toApiModel(client);
    }

    @Override
    @GetMapping(value = "/clients/{id}", produces = { "application/json" })
    public @ResponseBody Client getClient(@PathVariable("id") String id) {
        RegisteredClient client = registeredClientRepository.findById(id);
        checkClientWasFound(id, client);
        return toApiModel(client);
    }

    @Override
    @PutMapping(value = "/clients/{id}", produces = { "application/json" })
    public @ResponseBody Client updateClient(@PathVariable("id") String id,
            @RequestBody UpdateClient apiUpdateClient) {
        validateUpdateClientRequest(apiUpdateClient);

        RegisteredClient client = registeredClientRepository.findById(id);
        checkClientWasFound(id, client);
        checkClientIdIsNotTaken(id, apiUpdateClient.getClientId());

        client = toModel(id, apiUpdateClient);

        registeredClientRepository.save(client);

        return toApiModel(client);
    }

    @Override
    @DeleteMapping(value = "/clients/{id}", produces = { "application/json" })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable("id") String id) {
        RegisteredClient client = registeredClientRepository.findById(id);
        checkClientWasFound(id, client);
        registeredClientRepository.delete(client);
    }

    private void validateCreateClientRequest(CreateClient apiCreateClient) {
        validateClientId(apiCreateClient.getClientId());
        if (apiCreateClient.getClientSecret() != null) {
            validateClientSecret(apiCreateClient.getClientSecret());
        }
        validateClientName(apiCreateClient.getClientName());
        validateAuthorizationGrantTypes(apiCreateClient.getAuthorizationGrantTypes());
        validateRedirectUris(apiCreateClient.getRedirectUris());
        validateScopes(apiCreateClient.getScopes());
        if (apiCreateClient.getAccessTokenValidity() != null) {
            validateAccessTokenValidity(apiCreateClient.getAccessTokenValidity());
        }
        if (apiCreateClient.getRefreshTokenValidity() != null) {
            validateRefreshTokenValidity(apiCreateClient.getRefreshTokenValidity());
        }
    }

    private void validateUpdateClientRequest(UpdateClient apiUpdateClient) {
        validateClientId(apiUpdateClient.getClientId());
        validateClientSecret(apiUpdateClient.getClientSecret());
        validateClientName(apiUpdateClient.getClientName());
        validateAuthorizationGrantTypes(apiUpdateClient.getAuthorizationGrantTypes());
        validateRedirectUris(apiUpdateClient.getRedirectUris());
        validateScopes(apiUpdateClient.getScopes());
        validateAccessTokenValidity(apiUpdateClient.getAccessTokenValidity());
        validateRefreshTokenValidity(apiUpdateClient.getRefreshTokenValidity());
    }

    private void validateClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new ValidationException("clientId cannot be null or empty.");
        }
        if (clientId.length() > 100) {
            throw new ValidationException("clientId cannot exceed length of 100 characters.");
        }
        if (!clientId.matches("[a-z0-9\\-]+")) {
            throw new ValidationException("clientId can only contain 'a-z', '0-9' and '-'.");
        }
    }

    private void validateClientSecret(String clientSecret) {
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new ValidationException("clientSecret cannot be null or empty.");
        }
        if (clientSecret.length() > 200) {
            throw new ValidationException("clientSecret cannot exceed length of 200 characters.");
        }
        if (!clientSecret.matches("[A-Za-z0-9]+")) {
            throw new ValidationException("clientSecret can only contain 'A-Z', 'a-z', and '0-9'.");
        }
    }

    private void validateClientName(String clientName) {
        if (clientName == null || clientName.isEmpty()) {
            throw new ValidationException("clientName cannot be null or empty.");
        }
        if (clientName.length() > 200) {
            throw new ValidationException("clientName cannot exceed length of 200 characters.");
        }
        if (!clientName.matches("[A-Za-z0-9 +\\-/@#&()']+")) {
            throw new ValidationException("clientName can only contain 'A-Z', 'a-z', '0-9' and " +
                    "'+', '-', '/', '@', '#', '&', '(', ')', '''.");
        }
    }

    private void validateAuthorizationGrantTypes(Set<String> authorizationGrantTypes) {
        if (authorizationGrantTypes == null || authorizationGrantTypes.isEmpty()) {
            throw new ValidationException("authorizationGrantTypes cannot be null or empty.");
        }
        for (String gt : authorizationGrantTypes) {
            if (gt == null || gt.isEmpty()) {
                throw new ValidationException("authorizationGrantTypes cannot contain null or " +
                        "empty values.");
            }
            if (!Objects.equals(gt, AuthorizationGrantType.AUTHORIZATION_CODE.getValue()) &&
                    !Objects.equals(gt, (AuthorizationGrantType.REFRESH_TOKEN.getValue()))) {
                throw new ValidationException(String.format("'%s' is not a valid grant type.", gt));
            }
        }
    }

    private void validateRedirectUris(Set<String> redirectUris) {
        if (redirectUris == null || redirectUris.isEmpty()) {
            throw new ValidationException("redirectUris cannot be null or empty.");
        }
        for (String uri : redirectUris) {
            try {
                URI.create(uri);
            } catch (Exception e) {
                throw new ValidationException(String.format("'%s' is not a valid URI.", uri));
            }
        }
    }

    private void validateScopes(Set<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            throw new ValidationException("scopes cannot be null or empty.");
        }
        for (String s : scopes) {
            if (s == null || s.isEmpty()) {
                throw new ValidationException("scopes cannot contain null or empty values.");
            }
            if (!Objects.equals(s, AppConstants.OAUTH_SCOPE_OPENID) &&
                    !Objects.equals(s, AppConstants.OAUTH_SCOPE_READ) &&
                    !Objects.equals(s, AppConstants.OAUTH_SCOPE_WRITE)) {
                throw new ValidationException(String.format("'%s' is not a valid scope.", s));
            }
        }
    }

    private void validateAccessTokenValidity(Long validity) {
        validateTokenValidity("accessTokenValidity", validity);
    }

    private void validateRefreshTokenValidity(Long validity) {
        validateTokenValidity("refreshTokenValidity", validity);
    }

    private void validateTokenValidity(String name, Long validity) {
        if (validity == null) {
            throw new ValidationException(String.format("%s cannot be null.", name));
        }
        if (validity <= 0) {
            throw new ValidationException(String.format("%s cannot be negative or 0.", name));
        }
    }

    private void checkClientWasFound(String id, RegisteredClient client) {
        if (client == null) {
            throw new NotFoundException(String.format("A client with the ID '%s' was not found.",
                    id));
        }
    }

    private void checkClientIdIsNotTaken(String id, String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client != null && !Objects.equals(client.getId(), id)) {
            throw new NotFoundException(String.format("A client with the client ID '%s' does " +
                    "already exist.", clientId));
        }
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private String generateClientSecret() {
        return RandomStringUtils.random(CLIENT_SECRET_DEFAULT_LENGTH, true, true);
    }

    private static List<Client> toApiModel(List<RegisteredClient> clients) {
        List<Client> apiClients = new ArrayList<>();
        for (RegisteredClient client : clients) {
            apiClients.add(toApiModel(client));
        }
        return apiClients;
    }

    private static Client toApiModel(RegisteredClient client) {
        Client apiClient = new Client();
        apiClient.setId(client.getId());
        apiClient.setClientId(client.getClientId());
        apiClient.setClientSecret(removeClientSecretPrefix(client.getClientSecret()));
        apiClient.setClientName(client.getClientName());
        apiClient.setAuthorizationGrantTypes(client.getAuthorizationGrantTypes()
                .stream().map(AuthorizationGrantType::getValue).collect(Collectors.toSet()));
        apiClient.setRedirectUris(client.getRedirectUris());
        apiClient.setScopes(client.getScopes());
        apiClient.setIsRequireAuthorizationConsent(client.getClientSettings()
                .isRequireAuthorizationConsent());
        apiClient.setAccessTokenValidity(client.getTokenSettings().getAccessTokenTimeToLive()
                .toSeconds());
        apiClient.setRefreshTokenValidity(client.getTokenSettings().getRefreshTokenTimeToLive()
                .getSeconds());
        apiClient.setIsReuseRefreshTokens(client.getTokenSettings().isReuseRefreshTokens());
        return apiClient;
    }

    private static RegisteredClient toModel(String id, CreateClient apiCreateClient,
            String clientSecret) {
        return buildClient(id, apiCreateClient.getClientId(), clientSecret,
                apiCreateClient.getClientName(), apiCreateClient.getAuthorizationGrantTypes(),
                apiCreateClient.getRedirectUris(), apiCreateClient.getScopes(),
                apiCreateClient.getIsRequireAuthorizationConsent(),
                apiCreateClient.getAccessTokenValidity(), apiCreateClient.getRefreshTokenValidity(),
                apiCreateClient.getIsReuseRefreshTokens());
    }

    private static RegisteredClient toModel(String id, UpdateClient apiUpdateClient) {
        return buildClient(id, apiUpdateClient.getClientId(), apiUpdateClient.getClientSecret(),
                apiUpdateClient.getClientName(), apiUpdateClient.getAuthorizationGrantTypes(),
                apiUpdateClient.getRedirectUris(), apiUpdateClient.getScopes(),
                apiUpdateClient.getIsRequireAuthorizationConsent(),
                apiUpdateClient.getAccessTokenValidity(), apiUpdateClient.getRefreshTokenValidity(),
                apiUpdateClient.getIsReuseRefreshTokens());
    }

    private static RegisteredClient buildClient(String id, String clientId, String clientSecret,
            String clientName, Set<String> authorizationGrantTypes, Set<String> redirectUris,
            Set<String> scopes, Boolean isRequireAuthorizationConsent, Long accessTokenValidity,
            Long refreshTokenValidity, Boolean isReuseRefreshTokens) {
        ClientSettings.Builder clientSettingsBuilder = ClientSettings.builder();
        if (isRequireAuthorizationConsent != null) {
            clientSettingsBuilder.requireAuthorizationConsent(isRequireAuthorizationConsent);
        }
        ClientSettings clientSettings = clientSettingsBuilder.build();

        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.builder();
        if (accessTokenValidity != null && accessTokenValidity > 0L) {
            tokenSettingsBuilder.accessTokenTimeToLive(Duration.of(accessTokenValidity,
                    ChronoUnit.SECONDS));
        }
        if (refreshTokenValidity != null && refreshTokenValidity > 0L) {
            tokenSettingsBuilder.refreshTokenTimeToLive(Duration.of(refreshTokenValidity,
                    ChronoUnit.SECONDS));
        }
        if (isReuseRefreshTokens != null) {
            tokenSettingsBuilder.reuseRefreshTokens(isReuseRefreshTokens);
        }
        TokenSettings tokenSettings = tokenSettingsBuilder.build();

        return RegisteredClient.withId(id)
                .clientId(clientId)
                .clientSecret(addClientSecretPrefix(clientSecret))
                .clientName(clientName)
                .clientIdIssuedAt(Instant.now())
                .authorizationGrantTypes(c -> c.addAll(authorizationGrantTypes
                        .stream()
                        .map(AuthorizationGrantType::new)
                        .collect(Collectors.toSet())))
                .redirectUris(c -> c.addAll(redirectUris))
                .scopes(c -> c.addAll(scopes))
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .build();
    }

    private static String addClientSecretPrefix(String clientSecret) {
        return CLIENT_SECRET_PREFIX + clientSecret;
    }

    private static String removeClientSecretPrefix(String prefixedClientSecret) {
        if (prefixedClientSecret == null || !prefixedClientSecret.startsWith(CLIENT_SECRET_PREFIX)) {
            return prefixedClientSecret;
        }
        return prefixedClientSecret.substring(CLIENT_SECRET_PREFIX.length());
    }

}
