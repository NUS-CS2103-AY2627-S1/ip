package monday.exception;

/**
 * Signals an invalid command or missing command detail entered by the user.
 */
public class MondayException extends Exception {
    /**
     * Creates an exception with the specified message.
     */
    public MondayException(String message) {
        super(message);
    }
}
