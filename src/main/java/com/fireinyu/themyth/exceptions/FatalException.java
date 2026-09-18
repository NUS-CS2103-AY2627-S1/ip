package com.fireinyu.themyth.exceptions;

/**
 * An exception that should kill the app.
 */
public class FatalException extends RuntimeException {

    /**
     * Initializes a FatalException.
     *
     * @param message exception message.
     * @see String
     */
    public FatalException(String message) {
        super("DRAMA! The Myth has perished in absolute agony! Cause of this catastrophic demise: " + message);
    }
}
