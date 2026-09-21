package janet.exception;

/**
 * Base exception for errors specific to Janet's command handling.
 */
public class JanetException extends RuntimeException {
    /**
     * Creates a Janet-specific exception with a user-facing message.
     *
     * @param message the message to display to the user.
     */
    public JanetException(String message) {
        super(message);
    }

    /**
     * Creates a Janet-specific exception caused by another failure.
     *
     * @param message the message to display to the user.
     * @param cause underlying failure.
     */
    public JanetException(String message, Throwable cause) {
        super(message, cause);
    }
}
