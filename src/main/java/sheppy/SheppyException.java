package sheppy;

/** Represents a recoverable input or storage error with a user-facing explanation. */
public class SheppyException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation of the input error
     */
    public SheppyException(String message) {
        super(message);
    }

    /**
     * Creates an error while retaining its underlying cause for diagnostics.
     *
     * @param message the user-facing explanation
     * @param cause the underlying failure
     */
    public SheppyException(String message, Throwable cause) {
        super(message, cause);
    }
}
