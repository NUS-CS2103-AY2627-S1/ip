package groot.exception;

/**
 * Represents an input error that Groot can explain to the user.
 */
public class GrootException extends Exception {

    /**
     * Creates an exception with a user-facing explanation of the error.
     *
     * @param message Explanation to display to the user.
     */
    public GrootException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing explanation and its technical cause.
     *
     * @param message Explanation to display to the user.
     * @param cause Underlying failure retained for debugging.
     */
    public GrootException(String message, Throwable cause) {
        super(message, cause);
    }
}
