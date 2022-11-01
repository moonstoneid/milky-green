package com.moonstoneid.web3login.security;

import org.springframework.security.core.CredentialsContainer;

public class Web3Credentials implements CredentialsContainer {

    private String message;
    private String signature;
    private String nonce;

    public Web3Credentials(String message, String signature, String nonce) {
        this.message = message;
        this.signature = signature;
        this.nonce = nonce;
    }

    public String getMessage() {
        return message;
    }

    public String getSignature() {
        return signature;
    }

    public String getNonce() {
        return nonce;
    }

    @Override
    public void eraseCredentials() {
        message = null;
        signature = null;
        nonce = null;
    }

}
