package com.moonstoneid.web3login.api.controller;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import com.moonstoneid.web3login.api.doc.KeyPairApi;
import com.moonstoneid.web3login.api.model.KeyPairAM;
import com.moonstoneid.web3login.api.model.UpdateKeyPairAM;
import com.moonstoneid.web3login.utils.KeyPairUtils;
import com.moonstoneid.web3login.service.KeyPairService;
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

    private final KeyPairService keyPairService;

    private KeyPairController(KeyPairService keyPairService) {
        this.keyPairService = keyPairService;
    }

    @Override
    @GetMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM getKeyPair() {
        RSAKey keyPair = keyPairService.get();
        return toApiModel(keyPair);
    }

    @Override
    @PutMapping(value = "/keypair", produces = { "application/json" })
    public @ResponseBody KeyPairAM updateKeyPair(@RequestBody UpdateKeyPairAM apiUpdateKeyPair) {
        validateUpdateKeyPairRequest(apiUpdateKeyPair);

        RSAKey keyPair = toModel(apiUpdateKeyPair);

        keyPairService.save(keyPair);

        return toApiModel(keyPair);
    }

    private void validateUpdateKeyPairRequest(UpdateKeyPairAM apiUpdateKeyPair) {
        validatePublicKey(apiUpdateKeyPair.getPublicKey());
        validatePrivateKey(apiUpdateKeyPair.getPrivateKey());

        // TODO: validate if private key and public key generate a valid keypair
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

    private static KeyPairAM toApiModel(RSAKey keyPair) {
        try {
            KeyPairAM model = new KeyPairAM();
            model.setPublicKey(KeyPairUtils.toPCKS1String(keyPair.toRSAPublicKey()));
            model.setPrivateKey(KeyPairUtils.toPCKS1String(keyPair.toRSAPrivateKey()));
            return model;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private static RSAKey toModel(UpdateKeyPairAM apiUpdateKeyPair) {
        try {
            RSAPublicKey pubKey = KeyPairUtils.toRSAPublicKey(apiUpdateKeyPair.getPublicKey());
            RSAPrivateKey privKey = KeyPairUtils.toRSAPrivateKey(apiUpdateKeyPair.getPrivateKey());
            return new RSAKey.Builder(pubKey).privateKey(privKey).build();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
