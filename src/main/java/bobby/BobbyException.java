package bobby;

/**
 * Represents an error caused by invalid user input in Bobby.
 */
public class BobbyException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message explanation of what went wrong.
     */
    public BobbyException(String message) {
        super(message);
    }
}
