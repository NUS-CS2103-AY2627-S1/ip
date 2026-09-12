package minweeder.exception;

/**
 * Signals a user-facing error, such as invalid input or a failed file operation.
 */
public class MinweederException extends Exception {
    /**
     * Creates an exception with a message describing the problem to show the user.
     *
     * @param message the user-facing error message.
     */
    public MinweederException(String message) {
        super(message);
    }
}
