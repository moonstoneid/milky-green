package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.jose.Jwks;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
public class CustomJWKSource implements JWKSource<SecurityContext> {

    private RSAKey rsaKey = Jwks.generateRsa();

    @Override
    public List<JWK> get(JWKSelector jwkSelector, SecurityContext securityContext) throws KeySourceException {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return jwkSelector.select(jwkSet);
    }

}