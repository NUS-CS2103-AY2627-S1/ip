package mochi.exception;

/**
 * Represents a recoverable error caused by an invalid Mochi command.
 */
public class MochiException extends Exception {
    /** Serialization version for this exception class. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly explanation.
     *
     * @param message explanation of the invalid command and how to correct it
     */
    public MochiException(String message) {
        super(message);
    }
}
