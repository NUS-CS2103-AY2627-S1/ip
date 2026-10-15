package jarvis.exceptions;

/**
 * Represents an invalid or missing date/time value.
 */
public class InvalidDateAndTimeException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public InvalidDateAndTimeException(String message) {
        super(message);
    }
}
