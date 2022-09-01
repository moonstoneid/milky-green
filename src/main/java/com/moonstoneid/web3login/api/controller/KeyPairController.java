package com.moonstoneid.web3login.api.controller;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import com.moonstoneid.web3login.api.doc.KeyPairApi;
import com.moonstoneid.web3login.api.model.KeyPairAM;
import com.moonstoneid.web3login.api.model.UpdateKeyPairAM;
import com.moonstoneid.web3login.config.CustomJWKSource;
import com.moonstoneid.web3login.jose.KeyPairUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class KeyPairController implements KeyPairApi {

    private final CustomJWKSource jwkSource;

    private KeyPairController(CustomJWKSource jwkSource) {
        this.jwkSource = jwkSource;
    }

    @Override
    @GetMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM getKeyPair() {
        RSAKey keyPair = jwkSource.getRsaKey();
        try {
            return toApiModel(keyPair);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PutMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM updateKeyPair(@RequestBody UpdateKeyPairAM updateKeyPair) {
        validateUpdateKeyPairRequest(updateKeyPair);
        try {
            RSAPublicKey pubKey = KeyPairUtils.toRSAPublicKey(updateKeyPair.getPublicKey());
            RSAPrivateKey privKey = KeyPairUtils.toRSAPrivateKey(updateKeyPair.getPrivateKey());
            RSAKey keyPair = new RSAKey.Builder(pubKey).privateKey(privKey).build();
            jwkSource.setRsaKey(keyPair);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

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

    private static KeyPairAM toApiModel(RSAKey keyPair) throws JOSEException {
        KeyPairAM model = new KeyPairAM();
        model.setPublicKey(KeyPairUtils.toPCKS1String(keyPair.toRSAPublicKey()));
        model.setPrivateKey(KeyPairUtils.toPCKS1String(keyPair.toRSAPrivateKey()));
        return model;
    }

}
