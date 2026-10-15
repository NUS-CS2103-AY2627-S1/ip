package jeryl.exception;

/**
 * Signals that user input could not be understood or acted on, e.g. an
 * unknown command or a command missing required arguments.
 */
public class JerylException extends Exception {
    /**
     * Creates a JerylException with a user-facing message.
     */
    public JerylException(String message) {
        super(message);
    }
}
