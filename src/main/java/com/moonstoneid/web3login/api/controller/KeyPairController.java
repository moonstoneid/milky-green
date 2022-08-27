package com.moonstoneid.web3login.api.controller;

import java.security.spec.InvalidKeySpecException;

import com.moonstoneid.web3login.api.doc.KeyPairApi;
import com.moonstoneid.web3login.api.model.KeyPairAM;
import com.moonstoneid.web3login.api.model.UpdateKeyPairAM;
import com.moonstoneid.web3login.jose.KeyPairUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class KeyPairController implements KeyPairApi {

    @Override
    @GetMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM getKeyPair() {

        return null;
    }

    @Override
    @PutMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM updateKeyPair(@RequestBody UpdateKeyPairAM updateKeyPair) {
        validateUpdateKeyPairRequest(updateKeyPair);
        return null;
    }

    private void validateUpdateKeyPairRequest(UpdateKeyPairAM updateKeyPair) {
        validatePublicKey(updateKeyPair.getPublicKey());
        validatePrivateKey(updateKeyPair.getPrivateKey());
    }

    private void validatePublicKey(String publicKey) {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new ValidationException("publicKey cannot be null or empty.");
        }
        try {
            KeyPairUtils.toRSAPublicKey(publicKey);
        } catch (InvalidKeySpecException e) {
            throw new ValidationException("publicKey cannot be parsed.");
        }
    }

    private void validatePrivateKey(String privateKey) {
        if (privateKey == null || privateKey.isEmpty()) {
            throw new ValidationException("privateKey cannot be null or empty.");
        }
        try {
            KeyPairUtils.toRSAPrivateKey(privateKey);
        } catch (InvalidKeySpecException e) {
            throw new ValidationException("privateKey cannot be parsed.");
        }
    }

}
