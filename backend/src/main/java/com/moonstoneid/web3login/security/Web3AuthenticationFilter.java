package com.moonstoneid.web3login.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class Web3AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public Web3AuthenticationFilter(String loginPath) {
        super(new AntPathRequestMatcher(loginPath, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        Web3AuthenticationToken authRequest = Web3AuthRequestExtractor.extractAuthenticationRequest(
                request);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return getAuthenticationManager().authenticate(authRequest);
    }

}
