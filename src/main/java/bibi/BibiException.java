package bibi;

/**
 * Signals that a user command cannot be processed because its input is invalid.
 */
public class BibiException extends Exception {

    /**
     * Creates an exception containing a helpful message for the user.
     *
     * @param message explanation of how the input should be corrected
     */
    public BibiException(String message) {
        super(message);
    }
}
