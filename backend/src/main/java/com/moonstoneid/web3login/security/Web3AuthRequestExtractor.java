package com.moonstoneid.web3login.security;

import javax.servlet.http.HttpServletRequest;

import com.moonstoneid.web3login.AppConstants;
import org.springframework.util.Base64Utils;

public class Web3AuthRequestExtractor {

    private static final String MESSAGE_PARAMETER = "message";
    private static final String SIGNATURE_PARAMETER = "signature";
    private static final String NONCE_PARAMETER = "nonce";

    private Web3AuthRequestExtractor() {

    }

    public static Web3AuthenticationToken extractAuthenticationRequest(HttpServletRequest request) {
        String message = request.getParameter(MESSAGE_PARAMETER);
        message = message != null ? new String(Base64Utils.decodeFromString(message)) : null;

        String signature = request.getParameter(SIGNATURE_PARAMETER);
        signature = signature != null ? new String(Base64Utils.decodeFromString(signature)) : null;

        String nonce = (String) request.getSession().getAttribute(NONCE_PARAMETER);

        String domain = AppConstants.DOMAIN;

        return new Web3AuthenticationToken(new Web3Credentials(message, signature, nonce, domain));
    }

}
