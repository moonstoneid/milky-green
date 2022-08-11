package org.mn.web3login.security;

import java.util.Collection;
import java.util.List;

import org.mn.web3login.AppConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ApiKeyAuthenticationToken implements Authentication {

    private final List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(AppConstants.API_KEY_ROLE));

    @Override
    public String getName() {
        return AppConstants.API_KEY_PRINCIPAL;
    }

    @Override
    public Object getPrincipal() {
        return AppConstants.API_KEY_PRINCIPAL;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getDetails() {
        return null;
    }

}
