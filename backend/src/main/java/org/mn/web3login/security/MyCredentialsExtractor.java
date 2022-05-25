package org.mn.web3login.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Base64Utils;

public class MyCredentialsExtractor {

    private static final String MESSAGE_PARAMETER = "message";
    private static final String SIGNATURE_PARAMETER = "signature";
    private static final String NONCE_PARAMETER = "nonce";

    public static String[] extractCredentials(HttpServletRequest request){
        String[] credentials = new String[3];

        String message = request.getParameter(MESSAGE_PARAMETER);
        message = message != null ? new String(Base64Utils.decodeFromString(message)) : "";
        credentials[0] = message;

        String signature = request.getParameter(SIGNATURE_PARAMETER);
        signature = signature != null ? new String(Base64Utils.decodeFromString(signature)) : "";
        credentials[1] = signature;

        credentials[2] = (String) request.getSession().getAttribute(NONCE_PARAMETER);

        return credentials;
    }

}
