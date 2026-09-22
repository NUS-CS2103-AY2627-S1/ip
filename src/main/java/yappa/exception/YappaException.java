package yappa.exception;

/**
 * Represents application-specific exceptions thrown by the Yappa application
 * when encountering invalid user inputs or execution errors.
 */
public class YappaException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message Explanation of the error.
     */
    public YappaException(String message) {
        super(message);
    }

    /**
     * Creates an exception with an explanation and underlying cause.
     *
     * @param message Explanation of the error.
     * @param cause Underlying cause of the error.
     */
    public YappaException(String message, Throwable cause) {
        super(message, cause);
    }
}
