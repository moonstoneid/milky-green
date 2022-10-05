package com.moonstoneid.web3login.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

import com.moonstoneid.web3login.utils.KeyPairUtils;
import com.moonstoneid.web3login.model.KeyPair;
import com.moonstoneid.web3login.repo.KeyPairRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class KeyPairService {

    private final KeyPairRepository keyPairRepository;

    public KeyPairService(KeyPairRepository keyPairRepository) {
        this.keyPairRepository = keyPairRepository;

        // On instantiation, check if key pair exists in DB, otherwise a key pair is generated
        init();
    }

    private void init(){
        get();
    }
    
    public void save(RSAKey rsaKey) {
        Assert.notNull(rsaKey, "rsaKey cannot be empty");
        keyPairRepository.save(toEntity(rsaKey));
    }
    
    public RSAKey get() {
        RSAKey rsaKey;
        Optional<KeyPair> keyPairOptional = keyPairRepository.findById("1");
        if (keyPairOptional.isEmpty()) {
            rsaKey = generateRsaKey();
            keyPairRepository.save(toEntity(rsaKey));
        } else {
            rsaKey = toObject(keyPairOptional.get());
        }
        return rsaKey;
    }

    private KeyPair toEntity(RSAKey rsaKey) {
        try {
            KeyPair keyPair = new KeyPair();
            keyPair.setId("1");
            keyPair.setPrivateKey(KeyPairUtils.toPCKS1String((RSAPrivateKey) rsaKey.toPrivateKey()));
            keyPair.setPublicKey(KeyPairUtils.toPCKS1String((RSAPublicKey) rsaKey.toPublicKey()));
            return keyPair;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private RSAKey toObject(KeyPair rsaKey) {
        try {
            RSAPublicKey pubKey = KeyPairUtils.toRSAPublicKey(rsaKey.getPublicKey());
            RSAPrivateKey privKey = KeyPairUtils.toRSAPrivateKey(rsaKey.getPrivateKey());
            return new RSAKey.Builder(pubKey).privateKey(privKey).build();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAKey generateRsaKey() {
        java.security.KeyPair keyPair = KeyPairUtils.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

}