package com.moonstoneid.web3login.siwe;

import java.time.OffsetDateTime;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import com.moonstoneid.web3login.AppProperties;
import org.springframework.stereotype.Component;

@Component
public class SiweMessageCreator {

    private final String signingDomain;
    private final String signingUri;

    public SiweMessageCreator(AppProperties appProperties) {
        AppProperties.Service service = appProperties.getService();
        this.signingDomain = service.getDomain();
        this.signingUri = service.getProtocol() + "://" + service.getDomain();
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
