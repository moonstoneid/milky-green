package com.moonstoneid.web3login.security;

import java.io.Serializable;
import java.security.Principal;

import org.springframework.security.core.userdetails.UserDetails;

public class Web3Principal implements Principal, Serializable {

    private final String name;
    private final UserDetails userDetails;

    public Web3Principal(String name, UserDetails userDetails) {
        this.name = name;
        this.userDetails = userDetails;
    }

    @Override
    public String getName() {
        return name;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

}
