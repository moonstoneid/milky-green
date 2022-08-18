package com.moonstoneid.web3login.view.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.moonstoneid.web3login.security.Web3AuthenticationToken;
import com.moonstoneid.web3login.security.Web3Principal;
import com.moonstoneid.web3login.service.JpaAuthorizationConsentService;
import com.moonstoneid.web3login.service.JpaAuthorizationService;
import com.moonstoneid.web3login.service.JpaRegisteredClientRepository;
import com.moonstoneid.web3login.view.model.AuthorizationVM;
import com.moonstoneid.web3login.view.model.AuthorizationConsentVM;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private static final String PATH_HOME = "/";
    private static final String PATH_REVOKE_AUTH_CONSENT = "/perform-revoke-auth-consent";
    private static final String PATH_DELETE_AUTH = "/perform-delete-auth";

    private static final String REDIRECT_HOME = "redirect:/";

    private static final String VIEW_LOGIN = "login";
    private static final String VIEW_HOME = "home";

    private static final String ATTRIBUTE_USERNAME = "username";
    private static final String ATTRIBUTE_CONSENTS = "consents";
    private static final String ATTRIBUTE_AUTHORIZATIONS = "authorizations";

    private static final String FORM_PARAM_CLIENT_ID = "client_id";
    private static final String FORM_PARAM_AUTHORIZATION_ID = "authorization_id";

    private final JpaRegisteredClientRepository clientRepository;
    private final JpaAuthorizationService authorizationService;
    private final JpaAuthorizationConsentService authorizationConsentService;

    public HomeController(JpaRegisteredClientRepository clientRepository,
            JpaAuthorizationService authorizationService,
            JpaAuthorizationConsentService authorizationConsentService) {
        this.clientRepository = clientRepository;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
    }

    @GetMapping(value = PATH_HOME)
    public String home(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return VIEW_LOGIN;
        }

        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Principal web3Principal = web3Authentication.getPrincipal();
        String web3PrincipalName = web3Principal.getName();
        model.addAttribute(ATTRIBUTE_USERNAME, web3PrincipalName);

        List<RegisteredClient> clients = clientRepository.getAll();
        List<OAuth2Authorization> authorizations = authorizationService.getByPrincipalName(
                web3PrincipalName);
        List<OAuth2AuthorizationConsent> consents = authorizationConsentService.getByPrincipalName(
                web3PrincipalName);

        List<AuthorizationConsentVM> consentVMs = createAuthorizationConsentVMs(clients, consents);
        List<AuthorizationVM> authorizationVMs = createAuthorizationVMs(clients, authorizations);

        model.addAttribute(ATTRIBUTE_CONSENTS, consentVMs);
        model.addAttribute(ATTRIBUTE_AUTHORIZATIONS, authorizationVMs);

        return VIEW_HOME;
    }

    private List<AuthorizationConsentVM> createAuthorizationConsentVMs(List<RegisteredClient> clients,
            List<OAuth2AuthorizationConsent> consents) {
        ArrayList<AuthorizationConsentVM> consentVMs = new ArrayList<>();

        for (OAuth2AuthorizationConsent consent : consents) {
            AuthorizationConsentVM consentVM = new AuthorizationConsentVM();
            consentVM.setClientId(consent.getRegisteredClientId());
            consentVM.setClientName(findClient(clients, consent.getRegisteredClientId()));
            consentVM.setScopes(StringUtils.collectionToDelimitedString(consent.getScopes(), ", "));
            consentVMs.add(consentVM);
        }

        return consentVMs;
    }

    private List<AuthorizationVM> createAuthorizationVMs(List<RegisteredClient> clients,
            List<OAuth2Authorization> authorizations) {
        ArrayList<AuthorizationVM> authorizationVMs = new ArrayList<>();

        for (OAuth2Authorization authorization : authorizations) {
            AuthorizationVM authorizationVM = new AuthorizationVM();
            authorizationVM.setId(authorization.getId());

            authorizationVM.setClientName(findClient(clients, authorization.getRegisteredClientId()));

            Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
            Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();

            if (accessToken == null) {
                continue;
            }

            OAuth2Token token = accessToken.getToken();
            if (refreshToken != null) {
                token = refreshToken.getToken();
            }

            authorizationVM.setIssuedAt(formatInstant(token.getIssuedAt()));
            authorizationVM.setExpireAt(formatInstant(token.getExpiresAt()));

            authorizationVMs.add(authorizationVM);
        }

        return authorizationVMs;
    }

    private static String findClient(List<RegisteredClient> clients, String clientId) {
        Optional<RegisteredClient> client = clients.stream()
                .filter(c -> Objects.equals(c.getId(), clientId))
                .findFirst();
        return client.isPresent() ? client.get().getClientName() : "";
    }

    @PostMapping(value = PATH_REVOKE_AUTH_CONSENT)
    public String performRevokeAuthorizationConsents(HttpServletRequest request) {
        if (isAnonymous()) {
            return REDIRECT_HOME;
        }

        String web3PrincipalName = getCurrentPrincipalName();
        List<OAuth2AuthorizationConsent> consents = authorizationConsentService.getByPrincipalName(
                web3PrincipalName);

        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] clientIds = parameterMap.get(FORM_PARAM_CLIENT_ID);

        deleteAuthorizationConsents(consents, Arrays.asList(clientIds));

        return REDIRECT_HOME;
    }

    private void deleteAuthorizationConsents(List<OAuth2AuthorizationConsent> consents,
            List<String> clientIds) {
        for (OAuth2AuthorizationConsent consent : consents) {
            if (clientIds.contains(consent.getRegisteredClientId())) {
                authorizationConsentService.remove(consent);
            }
        }
    }

    @PostMapping(value = PATH_DELETE_AUTH)
    public String performDeleteAuthorizations(HttpServletRequest request) {
        if (isAnonymous()) {
            return REDIRECT_HOME;
        }

        String web3PrincipalName = getCurrentPrincipalName();
        List<OAuth2Authorization> authorizations = authorizationService.getByPrincipalName(
                web3PrincipalName);

        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] authorizationIds = parameterMap.get(FORM_PARAM_AUTHORIZATION_ID);

        deleteAuthorizations(authorizations, Arrays.asList(authorizationIds));

        return REDIRECT_HOME;
    }

    private void deleteAuthorizations(List<OAuth2Authorization> authorizations,
            List<String> deleteAuthorizationIds) {
        for (OAuth2Authorization authorization : authorizations) {
            if (deleteAuthorizationIds.contains(authorization.getId())) {
                authorizationService.remove(authorization);
            }
        }
    }

    private boolean isAnonymous() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication instanceof AnonymousAuthenticationToken;
    }

    private String getCurrentPrincipalName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Principal web3Principal = web3Authentication.getPrincipal();
        return web3Principal.getName();
    }

    private static String formatInstant(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

}
