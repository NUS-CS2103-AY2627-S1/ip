package samantha.exception;

/**
 * Indicates that user-supplied command input is invalid.
 */
public class InputException extends SamanthaException {
    /**
     * Creates an input exception with a user-facing message.
     *
     * @param message explanation of the invalid input
     */
    public InputException(String message) {
        super(message);
    }
}
