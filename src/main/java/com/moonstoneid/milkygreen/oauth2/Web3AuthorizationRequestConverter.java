package com.moonstoneid.milkygreen.oauth2;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import com.moonstoneid.milkygreen.eth.SiweMessageVerifier;
import com.moonstoneid.milkygreen.security.Web3AuthenticationToken;
import com.moonstoneid.milkygreen.security.Web3AuthRequestExtractor;
import com.moonstoneid.milkygreen.security.Web3Credentials;
import com.moonstoneid.milkygreen.security.Web3Principal;
import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class Web3AuthorizationRequestConverter implements AuthenticationConverter {

    private static final String DEFAULT_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    private final OAuth2AuthorizationCodeRequestAuthenticationConverter baseConverter =
            new OAuth2AuthorizationCodeRequestAuthenticationConverter();

    private final SiweMessageVerifier messageVerifier;

    public Web3AuthorizationRequestConverter(SiweMessageVerifier messageVerifier) {
        this.messageVerifier = messageVerifier;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (Objects.equals(request.getMethod(), "POST")) {
            authenticate(request);
        }

        return baseConverter.convert(request);
    }

    private void authenticate(HttpServletRequest request) {
        Web3AuthenticationToken authentication = Web3AuthRequestExtractor
                .extractAuthenticationRequest(request);
        Web3Credentials credentials = authentication.getCredentials();

        if (credentials.getMessage().isEmpty()) {
            return;
        }

        // Try to parse the message
        SiweMessage message = null;
        try {
            message = messageVerifier.parseMessage(credentials.getMessage());
        } catch (SiweException e) {
            throwError("Malformed message! " + e.getMessage());
        }

        // Check address
        String messageAddress = message.getAddress();
        String userAddress = getUserAddress();
        if (!Objects.equals(messageAddress, userAddress)) {
            throw new BadCredentialsException("Address does not match!");
        }

        // Try to validate signature
        // Throws an exception if signature is invalid, mandatory fields are missing, expiration has
        // been reached or now < notBefore
        try {
            messageVerifier.verifyMessage(message, credentials.getNonce(), credentials.getSignature());
        } catch (SiweException e) {
            switch (e.getErrorType()) {
                case DOMAIN_MISMATCH:
                    throwError("Domain does not match!");
                case NONCE_MISMATCH:
                    throwError("Nonce does not match!");
                case EXPIRED_MESSAGE:
                    throwError("Message expired!");
                case NOT_YET_VALID_MESSAGE:
                    throwError("Message not valid yet!");
                case INVALID_SIGNATURE:
                    throwError("Invalid signature");
                default:
                    throwError("Unknown error!");
            }
        }
    }

    private String getUserAddress() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Web3AuthenticationToken web3Authentication = (Web3AuthenticationToken) authentication;
        Web3Principal web3Principal = web3Authentication.getPrincipal();
        return web3Principal.getName();
    }

    private static void throwError(String description) {
        OAuth2Error error = new OAuth2Error("invalid_message", description, DEFAULT_ERROR_URI);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
    }

}
