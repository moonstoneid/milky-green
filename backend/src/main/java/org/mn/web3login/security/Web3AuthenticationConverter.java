package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;

import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class Web3AuthenticationConverter implements AuthenticationConverter {

    private UserDetailsService userDetailsService;

    public Web3AuthenticationConverter() {

    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        OAuth2AuthorizationCodeRequestAuthenticationConverter delegate =
                new OAuth2AuthorizationCodeRequestAuthenticationConverter();
        OAuth2AuthorizationCodeRequestAuthenticationToken authCodeAuthentication =
                (OAuth2AuthorizationCodeRequestAuthenticationToken) delegate.convert(request);

        // Check signature for POST /oauth2/authorize requests, not for GET /oauth2/authorize requests
        if ("POST".equals(request.getMethod())){
            authenticate(request);
        }

        return authCodeAuthentication;
    }

    private void authenticate(HttpServletRequest request) {
        Web3AuthenticationToken web3Authentication = Web3AuthRequestExtractor
                .extractAuthenticationRequest(request);

        Web3Credentials credentials = web3Authentication.getCredentials();
        String domain = credentials.getDomain();

        // Try to parse the message
        // Throws an exception if message is not a valid EIP-4361 message.
        SiweMessage siweMessage = null;
        try {
            siweMessage = new SiweMessage.Parser().parse(credentials.getMessage());
        } catch (SiweException e) {
            throwError("Malformed message! "+ e.getMessage());
        }

        String address = siweMessage.getAddress();

        // Throws UsernameNotFoundException
        UserDetails userDetails = userDetailsService.loadUserByUsername(address);

        // Try to validate signature
        // Throws an exception if signature is invalid, mandatory fields are missing, expiration has
        // been reached or now < notBefore
        try {
            // TODO: Fix domain check
            siweMessage.verify(credentials.getDomain(), credentials.getNonce(), credentials.getSignature());
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

    private static void throwError(String parameterName) {
        OAuth2Error error = new OAuth2Error("invalid_message", "OAuth 2.0 Parameter: " + parameterName,
                "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1");
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
    }

}
