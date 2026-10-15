package kiwi;

/**
 * Thrown when Kiwi cannot handle a user command or a save-file line is invalid.
 */
public class KiwiException extends Exception {
    /**
     * Creates an exception with a message to show the user.
     *
     * @param message friendly error text.
     */
    public KiwiException(String message) {
        super(message);
    }
}
