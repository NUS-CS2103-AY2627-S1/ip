package jarvis.exceptions;

/**
 * Represents an error raised while processing a Jarvis command.
 */
public class JarvisException extends Exception {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public JarvisException(String message) {
        super(message);
    }
}
