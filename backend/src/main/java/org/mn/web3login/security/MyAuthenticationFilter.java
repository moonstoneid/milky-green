package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = extractAuthenticationRequest(request);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return getAuthenticationManager().authenticate(authRequest);
    }

    private static UsernamePasswordAuthenticationToken extractAuthenticationRequest(
            HttpServletRequest request) {
        String username = request.getParameter(USERNAME_PARAMETER);
        username = username != null ? username : "";
        username = username.trim();
        String password = request.getParameter(PASSWORD_PARAMETER);
        password = password != null ? password : "";
        return new UsernamePasswordAuthenticationToken(username, password);
    }

}
