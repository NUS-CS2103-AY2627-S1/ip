package penguin.exception;

/**
 * Represents a recoverable validation, command, or persistence error in Penguin.
 */
public class PenguinException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message user-facing explanation of the failed operation.
     */
    public PenguinException(String message) {
        super(message);
    }
}
