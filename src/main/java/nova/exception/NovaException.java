package nova.exception;

/**
 * Represents an input or command error that Nova can explain to the user.
 */
public class NovaException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message explanation shown to the user.
     */
    public NovaException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-friendly explanation and its technical cause.
     *
     * @param message explanation shown to the user.
     * @param cause lower-level error that caused this exception.
     */
    public NovaException(String message, Throwable cause) {
        super(message, cause);
    }
}
