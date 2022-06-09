package org.mn.web3login.security;

import org.springframework.security.core.CredentialsContainer;

public class Web3Credentials implements CredentialsContainer {

    private String message;
    private String signature;
    private String nonce;
    private String domain;

    public Web3Credentials(String message, String signature, String nonce, String domain) {
        this.message = message;
        this.signature = signature;
        this.nonce = nonce;
        this.domain = domain;
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

    public String getDomain() {
        return domain;
    }

    @Override
    public void eraseCredentials() {
        message = null;
        signature = null;
        nonce = null;
        domain = null;
    }

}
