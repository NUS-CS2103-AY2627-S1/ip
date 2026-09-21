package topaz;

/**
 * Represents an error caused by invalid user input.
 */
public class TopazException extends Exception {
    /**
     * Creates an input error with the given message.
     *
     * @param message the message to display to the user
     */
    public TopazException(String message) {
        super(message);
    }
}
