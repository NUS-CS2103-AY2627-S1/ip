package windy.exception;

/**
 * Signals that a user's command does not follow the required input format.
 */
public class InvalidInputFormatException extends Exception {
    /**
     * Creates an exception with a message explaining the expected input format.
     *
     * @param message the explanation shown to the user
     */
    public InvalidInputFormatException(String message) {
        super(message);
    }
}
