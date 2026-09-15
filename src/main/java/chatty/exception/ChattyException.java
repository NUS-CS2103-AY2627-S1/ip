package chatty.exception;

/**
 * Represents an invalid command or task operation specific to Chatty.
 */
public class ChattyException extends Exception {
    /** Version identifier used when this exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a Chatty-specific exception with an explanation for the user.
     *
     * @param message explanation of the invalid input or operation.
     */
    public ChattyException(String message) {
        super(message);
    }
}
