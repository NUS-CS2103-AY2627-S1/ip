package sage.exception;

/**
 * Signals that user input or task operations are invalid.
 */
public class SageException extends Exception {
    /**
     * Creates a new exception with the given user-facing message.
     *
     * @param message The reason the command failed.
     */
    public SageException(String message) {
        super(message);
    }

    /**
     * Retains the underlying failure while presenting an actionable message to the user.
     *
     * @param message The user-facing explanation.
     * @param cause The underlying input or file-system failure.
     */
    public SageException(String message, Throwable cause) {
        super(message, cause);
    }
}
