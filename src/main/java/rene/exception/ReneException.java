package rene.exception;

/**
 * Represents an error caused by a command that Rene cannot process.
 */
public class ReneException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation.
     *
     * @param message the explanation to show to the user.
     */
    public ReneException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-friendly explanation and its underlying cause.
     *
     * @param message the explanation to show to the user.
     * @param cause the error that prevented Rene from processing the request.
     */
    public ReneException(String message, Throwable cause) {
        super(message, cause);
    }
}
