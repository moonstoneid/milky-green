package com.moonstoneid.milkygreen.eth;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;

public abstract class SiweMessageVerifier {

    private final SiweMessage.Parser messageParser = new SiweMessage.Parser();

    public SiweMessage parseMessage(String message) throws SiweException {
        return messageParser.parse(message);
    }

    public abstract void verifyMessage(SiweMessage message, String nonce, String signature)
            throws SiweException;

}
