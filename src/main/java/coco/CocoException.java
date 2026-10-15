package coco;

/**
 * Signals that user input could not be understood or acted on, e.g. a
 * missing description or an invalid date. The message is shown to the
 * user as-is.
 */
public class CocoException extends Exception {
    /**
     * Creates a CocoException with the given user-facing message.
     *
     * @param message Message to show the user.
     */
    public CocoException(String message) {
        super(message);
    }
}
