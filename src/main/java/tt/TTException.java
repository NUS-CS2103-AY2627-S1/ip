package tt;

/**
 * Represents an expected error while processing a user command.
 */
public class TTException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the command error.
     */
    public TTException(String message) {
        super(message);
    }
}
