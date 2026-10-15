package neil.exception;

/**
 * Represents an error that can be shown to a Neil user.
 */
public class NeilException extends Exception {
    /**
     * Creates an exception with the specified user-facing message.
     *
     * @param message explanation of the error.
     */
    public NeilException(String message) {
        super("Neil: " + message);
    }
}
