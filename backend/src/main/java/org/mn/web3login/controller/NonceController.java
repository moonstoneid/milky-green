package org.mn.web3login.controller;


import org.mn.web3login.siwe.util.Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NonceController {
    @GetMapping(value = "/nonce")
    public String getNonce() {
        return Utils.generateNonce();
    }

}
