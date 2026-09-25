package rice;

/**
 * Represents an error caused by invalid user input or command handling.
 */
public class RiceException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the error
     */
    public RiceException(String message) {
        super(message);
    }
}
