package com.moonstoneid.milkygreen.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class Web3AuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 5657048873402718453L;

    private final Web3Principal principal;
    private final Web3Credentials credentials;

    public Web3AuthenticationToken(Web3Credentials credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    public Web3AuthenticationToken(Web3Principal principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        super.setAuthenticated(true);
    }

    protected Web3AuthenticationToken(Web3Principal principal, Web3Credentials credentials,
            Collection<? extends GrantedAuthority> authorities, boolean isAuthenticated) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(isAuthenticated);
    }

    @Override
    public Web3Principal getPrincipal() {
        return principal;
    }

    @Override
    public Web3Credentials getCredentials() {
        return credentials;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted, use constructor to " +
                "create authenticated token.");
        super.setAuthenticated(false);
    }

}
