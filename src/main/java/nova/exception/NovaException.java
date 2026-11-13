package nova.exception;

/**
 * Represents an error that can be shown to a Nova user.
 */
public class NovaException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message Explanation of the error.
     */
    public NovaException(String message) {
        super(message);
    }
}
