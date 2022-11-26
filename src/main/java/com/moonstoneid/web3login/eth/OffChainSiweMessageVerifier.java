package com.moonstoneid.web3login.eth;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;

public class OffChainSiweMessageVerifier extends SiweMessageVerifier {

    private final String signingDomain;

    public OffChainSiweMessageVerifier(String signingDomain) {
        this.signingDomain = signingDomain;
    }

    @Override
    public void verifyMessage(SiweMessage message, String nonce, String signature)
            throws SiweException {
        message.verify(signingDomain, nonce, signature);
    }

}
