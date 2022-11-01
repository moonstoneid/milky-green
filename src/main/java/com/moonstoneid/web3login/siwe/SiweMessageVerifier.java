package com.moonstoneid.web3login.siwe;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import com.moonstoneid.web3login.AppProperties;
import org.springframework.stereotype.Component;

@Component
public class SiweMessageVerifier {

    private final SiweMessage.Parser messageParser = new SiweMessage.Parser();

    private final String signingDomain;

    public SiweMessageVerifier(AppProperties appProperties) {
        this.signingDomain = appProperties.getService().getDomain();
    }

    public SiweMessage parseMessage(String message) throws SiweException {
        return messageParser.parse(message);
    }

    public void verifyMessage(SiweMessage message, String nonce, String signature)
            throws SiweException {
        message.verify(signingDomain, nonce, signature);
    }

}
