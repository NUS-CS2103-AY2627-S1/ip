package charliek.exception;

/**
 * Base class for errors that can be reported by the CharlieK chatbot.
 */
public class CharlieKException extends Exception {
    /**
     * Creates a chatbot exception with the message shown to the user.
     *
     * @param message the custom error message.
     */
    public CharlieKException(String message) {
        super(message);
    }

    /**
     * Creates a chatbot exception with a message and underlying cause.
     *
     * @param message the custom error message.
     * @param cause the underlying cause of the error.
     */
    public CharlieKException(String message, Throwable cause) {
        super(message, cause);
    }
}
