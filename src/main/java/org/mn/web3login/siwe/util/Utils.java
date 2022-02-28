package org.mn.web3login.siwe.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    // The EIP-191 prefix
    private static final String GETH_SIGN_PREFIX = "\u0019Ethereum Signed Message:\n";

    /**
     * Validates the signature for the given message
     *
     * @param msg The message
     * @param sig The signature for the given message
     * @return If the signature is correct, it returns a List<String> of addresses. If incorrect, it returns an empty
     * List<String>
     */
    public static List<String> validate(String msg, String sig) {
        List<String> matchedAddresses = new ArrayList<>();
        String prefix = GETH_SIGN_PREFIX + msg.length();
        byte[] msgHash = Hash.sha3((prefix + msg).getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Numeric.hexStringToByteArray(sig);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        Sign.SignatureData sd = new Sign.SignatureData(v, (byte[]) Arrays.copyOfRange(signatureBytes, 0, 32),
                (byte[]) Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;
        boolean match = false;

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature((byte) i, new ECDSASignature(new BigInteger(1,
                    sd.getR()), new BigInteger(1, sd.getS())), msgHash);

            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                matchedAddresses.add(addressRecovered);
            }
        }
        return matchedAddresses;
    }

    /**
     * Generates a secure nonce for use in the SiweMessage to prevent replay attacks
     *
     * @return Nonce with an alphanumeric char set
     */
    public static String generateNonce() {
        return RandomStringUtils.random(20, 0, 0, true, true, null, new SecureRandom());
    }
}
