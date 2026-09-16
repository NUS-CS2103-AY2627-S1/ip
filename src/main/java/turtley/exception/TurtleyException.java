package turtley.exception;

/**
 * Represents an expected, user-facing error in Turtley.
 */
public class TurtleyException extends RuntimeException {

    /**
     * Creates a Turtley error with the explanation shown to the user.
     *
     * @param message the error explanation.
     */
    public TurtleyException(String message) {
        super(message);
    }

    /**
     * Creates a Turtley error while retaining the original cause.
     *
     * @param message the error explanation shown to the user.
     * @param cause the original exception.
     */
    public TurtleyException(String message, Throwable cause) {
        super(message, cause);
    }
}
