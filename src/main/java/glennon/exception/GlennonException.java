package glennon.exception;

/**
 * Signals that Glennon cannot process a user command and carries guidance
 * suitable for display in the text UI.
 */
public class GlennonException extends Exception {
    /** Serialization version for this exception type. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with user-facing guidance.
     *
     * @param message explanation of the input error and how to correct it.
     */
    public GlennonException(String message) {
        super(message);
    }

    /**
     * Creates an exception that preserves the lower-level parsing failure.
     *
     * @param message explanation of the input error and how to correct it.
     * @param cause parsing failure that caused this exception.
     */
    public GlennonException(String message, Throwable cause) {
        super(message, cause);
    }
}
