package org.mn.web3login.controller;


import org.mn.web3login.siwe.util.Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class NonceController {
    @GetMapping(value = "/nonce")
    public String getNonce(HttpServletRequest request) {
        String nonce = Utils.generateNonce();

        HttpSession session = request.getSession();
        session.setAttribute("nonce", nonce);
        return nonce;
    }

}
