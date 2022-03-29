package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mn.web3login.util.ParamUtil;
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
        String[] credentials = ParamUtil.parseCredentialsFromRequest(request);
        return new UsernamePasswordAuthenticationToken(null, credentials);
    }

}
