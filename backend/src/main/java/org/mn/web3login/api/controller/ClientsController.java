package org.mn.web3login.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mn.web3login.api.model.Client;
import org.mn.web3login.api.doc.ClientsApi;
import org.mn.web3login.service.JpaRegisteredClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class ClientsController implements ClientsApi {

    private static final String CLIENT_SECRET_PREFIX = "{noop}";

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
    @GetMapping(value = "/clients/{id}", produces = { "application/json" })
    public @ResponseBody Client getClient(@PathVariable("id") String id) {
        RegisteredClient client = registeredClientRepository.findById(id);
        checkClientWasFound(id, client);
        return toApiModel(client);
    }

    private void checkClientWasFound(String id, RegisteredClient client) {
        if (client == null) {
            throw new NotFoundException(String.format("A client with the ID '%s' was not found.",
                    id));
        }
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
