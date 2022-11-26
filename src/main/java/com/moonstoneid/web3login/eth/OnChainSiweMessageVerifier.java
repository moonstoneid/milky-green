package com.moonstoneid.web3login.eth;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import org.web3j.protocol.Web3j;

public class OnChainSiweMessageVerifier extends SiweMessageVerifier {

    private final String signingDomain;
    private final Web3j web3j;

    public OnChainSiweMessageVerifier(String signingDomain, Web3j web3j) {
        this.signingDomain = signingDomain;
        this.web3j = web3j;
    }

    @Override
    public void verifyMessage(SiweMessage message, String nonce, String signature)
            throws SiweException {
        message.verify(signingDomain, nonce, signature, web3j);
    }

}
