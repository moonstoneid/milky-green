package com.moonstoneid.milkygreen.eth;

import java.time.OffsetDateTime;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;

public class SiweMessageCreator {

    private final String signingDomain;
    private final String signingUri;

    public SiweMessageCreator(String serviceProtocol, String serviceDomain) {
        this.signingDomain = serviceDomain;
        this.signingUri = serviceProtocol + "://" + serviceDomain;
    }

    public String createMessage(int chainId, String address, String statement, String nonce)
            throws SiweException {
        SiweMessage message = new SiweMessage.Builder(signingDomain, address, signingUri, "1",
                chainId, nonce, OffsetDateTime.now().toString())
                .statement(statement)
                .build();
        return message.toMessage();
    }

}
