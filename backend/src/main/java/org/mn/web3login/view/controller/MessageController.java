package org.mn.web3login.view.controller;

import java.time.OffsetDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mn.web3login.AppConstants;
import org.mn.web3login.siwe.SiweMessage;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.siwe.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private static final String URI = "http://" + AppConstants.DOMAIN;

    private static final String LOGIN_STATEMENT = "Sign in to use the app.";
    private static final String CONSENT_STATEMENT = "Approve the following OAuth Client: %s";

    private int requestCounter = 0;

    @GetMapping(value = "/login-message")
    public ResponseEntity<String> getLoginMessage(HttpServletRequest request,
            @RequestParam("chain_id") String chainId, @RequestParam("address") String address) {
        // TODO: Validate chain ID
        int parsedChainId = Integer.parseInt(chainId);
        // TODO: Validate address
        String parsedAddress = address;

        String nonce = createNonce(request.getSession());

        try {
            String message = createMessage(parsedChainId, parsedAddress, LOGIN_STATEMENT, nonce);
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
            String message = createMessage(parsedChainId, parsedAddress,
                String.format(CONSENT_STATEMENT, parsedClientId), nonce);
            return ResponseEntity.status(200).body(message);
        } catch (SiweException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    private static String createNonce(HttpSession session) {
        String nonce = Utils.generateNonce();
        session.setAttribute("nonce", nonce);
        return nonce;
    }

    private static String createMessage(int chainId, String address, String statement,
        String nonce) throws SiweException {
        SiweMessage message = new SiweMessage.Builder(AppConstants.DOMAIN, address, URI, "1",
                chainId, nonce, OffsetDateTime.now().toString())
                .statement(statement)
                .build();
        return message.toMessage();
    }

}
