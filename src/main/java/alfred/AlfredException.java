package alfred;

/**
 * Signals a recoverable problem while handling a user command or the task list.
 */
public class AlfredException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message Explanation shown to the user.
     */
    public AlfredException(String message) {
        super(message);
    }
}
