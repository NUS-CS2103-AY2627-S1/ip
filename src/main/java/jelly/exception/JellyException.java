package jelly.exception;

/**
 * Represents an expected error encountered while processing a Jelly command.
 */
public class JellyException extends Exception {
    /**
     * Creates an exception with the supplied user-facing message.
     *
     * @param message the explanation of the error.
     */
    public JellyException(String message) {
        super(message);
    }
}
