package com.moonstoneid.web3login.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3login.jose.Jwks;
import com.moonstoneid.web3login.jose.KeyPairUtils;
import com.moonstoneid.web3login.model.KeyPair;
import com.moonstoneid.web3login.repo.KeyPairRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.stereotype.Component;

@Component
public class CustomJWKSource implements JWKSource<SecurityContext> {

    private KeyPairRepository keyPairRepository;

    public CustomJWKSource(KeyPairRepository keyPairRepository) {

        this.keyPairRepository = keyPairRepository;

        // On instatiation, check if KeyPair exists in DB, otherwise a KeyPair is generated
        initKeyPair();
    }

    @Override
    public List<JWK> get(JWKSelector jwkSelector, SecurityContext securityContext) throws KeySourceException {
        JWKSet jwkSet = new JWKSet(getKeyPair());
        return jwkSelector.select(jwkSet);
    }
    
    public KeyPair setKeyPair(RSAKey keyPair){
        return keyPairRepository.save(toEntity(keyPair));
    }
    
    public RSAKey getKeyPair(){
        RSAKey rsaKey = null;
        Optional<KeyPair> keyPairOptional = keyPairRepository.findById("1");
        if(keyPairOptional.isEmpty()){
            rsaKey = Jwks.generateRsa();
            keyPairRepository.save(toEntity(rsaKey));
        }
        else{
            rsaKey = toObject(keyPairOptional.get());
        }
        return rsaKey;
    }

    private void initKeyPair(){
        getKeyPair();
    }

    private KeyPair toEntity(RSAKey rsaKey) {

        KeyPair keyPair = new KeyPair();
        try {
            keyPair.setId("1");
            keyPair.setPrivateKey(KeyPairUtils.toPCKS1String((RSAPrivateKey) rsaKey.toPrivateKey()));
            keyPair.setPublicKey(KeyPairUtils.toPCKS1String((RSAPublicKey) rsaKey.toPublicKey()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return keyPair;
    }

    private RSAKey toObject(KeyPair rsaKey) {
        RSAKey keyPair = null;
        try {
            RSAPublicKey pubKey = KeyPairUtils.toRSAPublicKey(rsaKey.getPublicKey());
            RSAPrivateKey privKey = KeyPairUtils.toRSAPrivateKey(rsaKey.getPrivateKey());
            keyPair = new RSAKey.Builder(pubKey).privateKey(privKey).build();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return keyPair;
    }

}