package magnus.exception;

/**
 * Represents a checked error that Magnus can report to the user.
 */
public class MagnusException extends Exception {
    /**
     * Creates a Magnus exception with an explanatory message.
     *
     * @param message The detail message describing the error.
     */
    public MagnusException(String message) {
        super(message);
    }

    /**
     * Creates a Magnus exception with an explanatory message and underlying cause.
     *
     * @param message The detail message describing the error.
     * @param cause The underlying cause of the error.
     */
    public MagnusException(String message, Throwable cause) {
        super(message, cause);
    }
}
