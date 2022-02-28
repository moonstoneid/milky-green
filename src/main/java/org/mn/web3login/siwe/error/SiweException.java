package org.mn.web3login.siwe.error;

import lombok.Getter;

/**
 * Signals an error while working with Siwe messages
 */
public class SiweException extends Exception {

    private static final long serialVersionUID = 2642720416433996541L;

    @Getter
    private ErrorTypes mErrorType;

    /**
     * Constructs a new exception with the specified detail message and an ErrorType.
     * @param message The detailed message.
     * @param type The error type
     */
    public SiweException(String message, ErrorTypes type) {
        super(message);
        mErrorType = type;
    }

    /**
     * Constructs a new exception with the specified detail message and an ErrorType.
     * @param message The detailed message.
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A <code>null</code> value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     * @param type The error type
     */
    public SiweException(String message, Throwable cause, ErrorTypes type) {
        super(message, cause);
        mErrorType = type;
    }

}