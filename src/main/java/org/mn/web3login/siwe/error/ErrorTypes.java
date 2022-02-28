package org.mn.web3login.siwe.error;

public enum ErrorTypes {
    /**
     * Thrown when the `validate()` function can verify the message.
     */
    INVALID_SIGNATURE(-1, "Invalid signature."),

    /**
     * Thrown when the `expirationTime` is present and in the past.
     */
    EXPIRED_MESSAGE(-2, "Expired message."),

    /**
     * Thrown when some required field is missing.
     */
    MALFORMED_SESSION(-3, "Malformed session."),

    /**
     * Thrown when message is malformed and cannot be parsed.
     */
    MALFORMED_MESSAGE(-4, "Malformed message."),

    /**
     * Thrown when the `notBefore` is present and in the future.
     */
    NOTBEFORE_MESSAGE(-5, "Expired message.");

    private final int mNumber;
    private final String mText;

    /**
     * Constructs a new enumeration constant with the provided error number and message.
     *
     * @param number The error number.
     * @param text   The error message.
     */
    ErrorTypes(int number, String text) {
        mNumber = number;
        mText = text;
    }

}
