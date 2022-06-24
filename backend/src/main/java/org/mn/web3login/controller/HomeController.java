package org.mn.web3login.controller;

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

import org.mn.web3login.controller.model.AuthorizationVM;
import org.mn.web3login.security.Web3AuthenticationToken;
import org.mn.web3login.security.Web3Principal;
import org.mn.web3login.service.JpaAuthorizationService;
import org.mn.web3login.service.JpaRegisteredClientRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final JpaRegisteredClientRepository clientRepository;
    private final JpaAuthorizationService authorizationService;

    public HomeController(JpaRegisteredClientRepository clientRepository,
            JpaAuthorizationService authorizationService) {
        this.clientRepository = clientRepository;
        this.authorizationService = authorizationService;
    }

    @GetMapping(value = "/")
    public String home(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            return "home";
        }

        model.addAttribute("isAuthenticated", true);

        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Principal web3Principal = web3Authentication.getPrincipal();
        String web3PrincipalName = web3Principal.getName();
        model.addAttribute("username", web3PrincipalName);

        List<RegisteredClient> clients = clientRepository.getAll();
        List<OAuth2Authorization> authorizations = authorizationService.getByPrincipalName(
                web3PrincipalName);

        List<AuthorizationVM> authorizationVMs = createAuthorizationVMs(clients, authorizations);

        model.addAttribute("authorizations", authorizationVMs);

        return "home";
    }

    private List<AuthorizationVM> createAuthorizationVMs(List<RegisteredClient> clients,
            List<OAuth2Authorization> authorizations) {
        ArrayList<AuthorizationVM> authorizationVMs = new ArrayList<>();

        for (OAuth2Authorization authorization : authorizations) {
            AuthorizationVM authorizationVM = new AuthorizationVM();
            authorizationVM.setId(authorization.getId());

            Optional<RegisteredClient> client = clients.stream()
                    .filter(c -> Objects.equals(c.getId(), authorization.getRegisteredClientId()))
                    .findFirst();
            authorizationVM.setClientName(client.map(RegisteredClient::getClientName).orElse(null));

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

    @PostMapping(value = "/perform-delete-authorizations")
    public String performDeleteAuthorizations(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/";
        }

        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Principal web3Principal = web3Authentication.getPrincipal();
        String web3PrincipalName = web3Principal.getName();

        List<OAuth2Authorization> authorizations = authorizationService.getByPrincipalName(
                web3PrincipalName);

        String[] deleteAuthorizationIds = parameterMap.get("authorization_id");

        deleteAuthorizations(authorizations, Arrays.asList(deleteAuthorizationIds));

        return "redirect:/";
    }

    private void deleteAuthorizations(List<OAuth2Authorization> authorizations,
            List<String> deleteAuthorizationIds) {
        for (OAuth2Authorization authorization : authorizations) {
            if (deleteAuthorizationIds.contains(authorization.getId())) {
                authorizationService.remove(authorization);
            }
        }
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
