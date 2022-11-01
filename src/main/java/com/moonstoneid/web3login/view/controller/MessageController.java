package com.moonstoneid.web3login.view.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.moonstoneid.siwe.error.SiweException;
import com.moonstoneid.siwe.util.Utils;
import com.moonstoneid.web3login.siwe.SiweMessageCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private static final String LOGIN_STATEMENT = "Sign in to use the app.";
    private static final String CONSENT_STATEMENT = "Approve the following OAuth Client: %s";

    private final SiweMessageCreator messageCreator;

    public MessageController(SiweMessageCreator messageCreator) {
        this.messageCreator = messageCreator;
    }

    @GetMapping(value = "/login-message")
    public ResponseEntity<String> getLoginMessage(HttpServletRequest request,
            @RequestParam("chain_id") String chainId, @RequestParam("address") String address) {
        // TODO: Validate chain ID
        int parsedChainId = Integer.parseInt(chainId);
        // TODO: Validate address
        String parsedAddress = address;

        String nonce = createNonce(request.getSession());

        try {
            String message = messageCreator.createMessage(parsedChainId, parsedAddress,
                    LOGIN_STATEMENT, nonce);
            return ResponseEntity.status(200).body(message);
        } catch (SiweException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/consent-message")
    public ResponseEntity<String> getConsentMessage(HttpServletRequest request,
            @RequestParam("chain_id") String chainId, @RequestParam("address") String address,
            @RequestParam("client_id") String clientId) {
        // TODO: Validate chain ID
        int parsedChainId = Integer.parseInt(chainId);
        // TODO: Validate address
        String parsedAddress = address;
        // TODO: Validate client ID
        String parsedClientId = clientId;

        String nonce = createNonce(request.getSession());


        try {
            String message = messageCreator.createMessage(parsedChainId, parsedAddress,
                String.format(CONSENT_STATEMENT, parsedClientId), nonce);
            return ResponseEntity.status(200).body(message);
        } catch (SiweException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    private String createNonce(HttpSession session) {
        String nonce = Utils.generateNonce();
        session.setAttribute("nonce", nonce);
        return nonce;
    }

}
