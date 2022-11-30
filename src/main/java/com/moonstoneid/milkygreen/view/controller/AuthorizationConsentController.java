package com.moonstoneid.milkygreen.view.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.moonstoneid.milkygreen.AppConstants;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizationConsentController {

    public static final String PATH_CONSENT = "/oauth2/consent";

    private static final String VIEW_CONSENT = "consent";

    private static final String ATTRIBUTE_CLIENT_ID = "clientId";
    private static final String ATTRIBUTE_STATE = "state";
    private static final String ATTRIBUTE_SCOPES = "scopes";
    private static final String ATTRIBUTE_PREVIOUSLY_APPROVED_SCOPES = "previouslyApprovedScopes";
    private static final String ATTRIBUTE_PRINCIPAL_NAME = "principalName";

    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;

    public AuthorizationConsentController(RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationConsentService = authorizationConsentService;
    }

    @GetMapping(value = PATH_CONSENT)
    public String consent(Principal principal, Model model,
              @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
              @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
              @RequestParam(OAuth2ParameterNames.STATE) String state) {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
        OAuth2AuthorizationConsent currentAuthorizationConsent = authorizationConsentService
                .findById(registeredClient.getId(), principal.getName());

        Set<String> authorizedScopes;
        Set<String> previouslyApprovedScopes = new HashSet<>();
        Set<String> scopesToApprove = new HashSet<>();
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (requestedScope.equals(AppConstants.OAUTH_SCOPE_OPENID)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute(ATTRIBUTE_CLIENT_ID, clientId);
        model.addAttribute(ATTRIBUTE_STATE, state);
        model.addAttribute(ATTRIBUTE_SCOPES, withDescription(scopesToApprove));
        model.addAttribute(ATTRIBUTE_PREVIOUSLY_APPROVED_SCOPES, withDescription(
                previouslyApprovedScopes));
        model.addAttribute(ATTRIBUTE_PRINCIPAL_NAME, principal.getName());

        return VIEW_CONSENT;
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));
        }
        return scopeWithDescriptions;
    }

    public static class ScopeWithDescription {

        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(
                    AppConstants.OAUTH_SCOPE_PROFILE,
                    "This application will be able to read your profile."
            );
            scopeDescriptions.put(
                    AppConstants.OAUTH_SCOPE_EMAIL,
                    "This application will be able to read your email address."
            );
        }

        public final String scope;
        public final String description;

        public ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.get(scope);
        }

    }

}
