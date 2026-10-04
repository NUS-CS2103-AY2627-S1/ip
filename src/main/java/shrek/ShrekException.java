package shrek;

/**
 * Represents an exception specific to the Shrek chatbot.
 */
public class ShrekException extends Exception {
    /**
     * Creates a new ShrekException with the given message.
     *
     * @param message the error message.
     */
    public ShrekException(String message) {
        super(message);
    }
}
