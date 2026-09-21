package thaisbot;

/**
 * Signals that the user's input or application state is invalid.
 */
public class ThaisBotException extends Exception {
    /**
     * Creates an exception with the given message.
     * @param message error message
     */
    public ThaisBotException(String message) {
        super(message);
    }
}
