package bobby.exception;

/**
 * Represents an error caused by an invalid command entered into Bobby.
 */
public class BobbyException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message the explanation of the invalid command
     */
    public BobbyException(String message) {
        super(message);
    }
}
