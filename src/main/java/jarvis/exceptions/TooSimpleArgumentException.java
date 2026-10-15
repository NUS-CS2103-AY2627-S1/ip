package jarvis.exceptions;

/** Represents a input that is too general to work with. */
public class TooSimpleArgumentException extends JarvisException {
    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public TooSimpleArgumentException(String message) {
        super(message);
    }
}
