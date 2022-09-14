package com.moonstoneid.web3login.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyPairUtils {

    private static final String PKCS1_PUB_START = "-----BEGIN RSA PUBLIC KEY-----";
    private static final String PKCS1_PUB_END = "-----END RSA PUBLIC KEY-----";
    private static final String PKCS1_PRIV_START = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PKCS1_PRIV_END = "-----END RSA PRIVATE KEY-----";

    private KeyPairUtils() {

    }

    public static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    public static String toPCKS1String(RSAPublicKey pubKey){
        String b64PubKey = Base64.getEncoder().encodeToString(pubKey.getEncoded());
        return PKCS1_PUB_START + b64PubKey + PKCS1_PUB_END;
    }

    public static String toPCKS1String(RSAPrivateKey privKey){
        String b64PrivKey = Base64.getEncoder().encodeToString(privKey.getEncoded());
        return PKCS1_PRIV_START + b64PrivKey + PKCS1_PRIV_END;
    }

    public static RSAPublicKey toRSAPublicKey(String pkcs1String) throws InvalidKeySpecException {
        pkcs1String = pkcs1String.replace(PKCS1_PUB_START, "");
        pkcs1String = pkcs1String.replace(PKCS1_PUB_END, "");

        byte[] encoded = Base64.getDecoder().decode(pkcs1String);
        RSAPublicKey pubKey;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            pubKey = (RSAPublicKey) kf.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        return pubKey;
    }

    public static RSAPrivateKey toRSAPrivateKey(String pkcs1String) throws InvalidKeySpecException {
        pkcs1String = pkcs1String.replace(PKCS1_PRIV_START, "");
        pkcs1String = pkcs1String.replace(PKCS1_PRIV_END, "");

        byte[] encoded = Base64.getDecoder().decode(pkcs1String);
        RSAPrivateKey privKey;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec (encoded);
            privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        return privKey;
    }

}
