package avon.exception;

/**
 * Represents an error caused by an invalid input from the user.
 */
public class AvonException extends Exception {
    private static final String MESSAGE_PREFIX = "Pardon, I beseech thee! ";

    /**
     * Creates an Avon exception with guidance for the user.
     *
     * @param message the error-specific explanation shown to the user.
     */
    public AvonException(String message) {
        super(MESSAGE_PREFIX + message);
    }
}
