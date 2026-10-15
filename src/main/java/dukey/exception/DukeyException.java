package dukey.exception;

/**
 * Represents an error that Dukey can explain to the user.
 */
public class DukeyException extends Exception {
    /**
     * Creates an exception with a user-friendly error message.
     *
     * @param message explanation shown to the user.
     */
    public DukeyException(String message) {
        super(message);
    }
}
