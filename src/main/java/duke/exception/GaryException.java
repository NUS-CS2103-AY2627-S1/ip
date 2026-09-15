package duke.exception;

/**
 * Represents a user-facing error encountered while executing commands.
 *
 * <p>This is a runtime exception because it is used to control command flow and display an error message
 * to the user via the UI.</p>
 */
public class GaryException extends RuntimeException {
    /**
     * Creates an exception with the given message.
     *
     * @param message Message to show to the user.
     */
    public GaryException(String message) {
        super(message);
    }
}
