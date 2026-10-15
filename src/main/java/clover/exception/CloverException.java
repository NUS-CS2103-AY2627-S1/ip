package clover.exception;

/**
 * Represents an error caused by invalid Clover command or task input.
 */
public class CloverException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message suitable for showing to the user.
     */
    public CloverException(String message) {
        super(message);
    }
}
