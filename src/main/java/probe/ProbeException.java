package probe;

/**
 * Represents an expected, user-facing application error.
 */
public class ProbeException extends Exception {
    /**
     * Creates an exception with a message for the user.
     *
     * @param message Message describing the error.
     */
    public ProbeException(String message) {
        super(message);
    }
}
