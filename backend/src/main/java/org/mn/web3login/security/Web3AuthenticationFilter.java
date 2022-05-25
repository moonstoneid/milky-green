package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Base64Utils;

public class Web3AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String MESSAGE_PARAMETER = "message";
    private static final String SIGNATURE_PARAMETER = "signature";
    private static final String NONCE_PARAMETER = "nonce";

    public Web3AuthenticationFilter(String loginPath) {
        super(new AntPathRequestMatcher(loginPath, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        Web3AuthenticationToken authRequest = extractAuthenticationRequest(request);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return getAuthenticationManager().authenticate(authRequest);
    }

    private static Web3AuthenticationToken extractAuthenticationRequest(HttpServletRequest request) {
        String message = request.getParameter(MESSAGE_PARAMETER);
        message = message != null ? new String(Base64Utils.decodeFromString(message)) : null;

        String signature = request.getParameter(SIGNATURE_PARAMETER);
        signature = signature != null ? new String(Base64Utils.decodeFromString(signature)) : null;

        String nonce = (String) request.getSession().getAttribute(NONCE_PARAMETER);

        return new Web3AuthenticationToken(new Web3Credentials(message, signature, nonce));
    }

}
