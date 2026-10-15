package sherlock.exception;

/**
 * Represents an error caused by an invalid Sherlock command or command argument.
 */
public class SherlockException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid command or data
     */
    public SherlockException(String message) {
        super(message);
    }
}
