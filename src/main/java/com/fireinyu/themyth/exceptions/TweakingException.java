package com.fireinyu.themyth.exceptions;

/**
 * An exception that should be broadcasted, but should not block or kill the app.
 *
 * @see FatalException
 */
public abstract class TweakingException extends RuntimeException {

    /**
     * Initializes a TweakingException.
     *
     * @param message exception message.
     * @see String
     */
    public TweakingException(String message) {
        super("Honey, I am clutching my pearls because " + message);
    }
}
