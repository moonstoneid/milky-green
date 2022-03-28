package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Base64Utils;

import java.util.Base64;

public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String MESSAGE_PARAMETER = "message";
    private static final String SIGNATURE_PARAMETER = "signature";
    private static final String NONCE_PARAMETER = "nonce";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = extractAuthenticationRequest(request);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return getAuthenticationManager().authenticate(authRequest);
    }

    private static UsernamePasswordAuthenticationToken extractAuthenticationRequest(
            HttpServletRequest request) {
        String[] credentials = new String[3];

        String message = request.getParameter(MESSAGE_PARAMETER);
        message = message != null ? new String(Base64Utils.decodeFromString(message)) : "";
        credentials[0] = message;

        String signature = request.getParameter(SIGNATURE_PARAMETER);
        signature = signature != null ? new String(Base64Utils.decodeFromString(signature)) : "";
        credentials[1] = signature;

        credentials[2] = (String) request.getSession().getAttribute(NONCE_PARAMETER);

        return new UsernamePasswordAuthenticationToken(null, credentials);
    }

}
