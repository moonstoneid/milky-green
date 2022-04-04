package org.mn.web3login.security;

import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.util.ParamUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;

import javax.servlet.http.HttpServletRequest;

public class MyAuthenticationConverter implements AuthenticationConverter {

    private UserDetailsService userDetailsService;

    public MyAuthenticationConverter (){}

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
                    throwError("invalid_message", "Invalid signature");
                case EXPIRED_MESSAGE:
                    throwError("invalid_message", "Message expired");
                case MALFORMED_SESSION:
                    throwError("invalid_message", "Malformed session");
                case MALFORMED_MESSAGE:
                    throwError("invalid_message", "Malformed message");
                case NOTBEFORE_MESSAGE:
                    throwError("invalid_message", "Message not yet valid");
                default:
                    throwError("invalid_message", "Unknown error");
            }
        }

        // Check nonce
        if(!nonce.equals(siweMessage.getMNonce())){
            throwError("invalid_message", "Incorrect nonce");
        }
    }

    private static void throwError(String errorCode, String parameterName) {
        throwError(errorCode, parameterName, "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1");
    }

    private static void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, (OAuth2AuthorizationCodeRequestAuthenticationToken) null);
    }

}
