package tony.exception;

/**
 * Represents an error caused by a command entered into the Tony chatbot.
 */
public class TonyException extends Exception {
    /**
     * Creates an exception with a message that tells the user how to fix their command.
     *
     * @param message the user-facing explanation of the command error.
     */
    public TonyException(String message) {
        super(message);
    }
}
