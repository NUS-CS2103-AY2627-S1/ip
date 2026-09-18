package friday.exception;

/**
 * Signals an expected, user-correctable error while processing a Friday command.
 */
public class FridayException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message Explanation of the error.
     */
    public FridayException(String message) {
        super(message);
    }
}
