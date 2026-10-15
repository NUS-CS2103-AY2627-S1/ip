package jarvis.exceptions;

/**
 * Represents a command that is missing required parameters.
 */
public class IncompleteCommandException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public IncompleteCommandException(String message) {
        super(message);
    }
}
