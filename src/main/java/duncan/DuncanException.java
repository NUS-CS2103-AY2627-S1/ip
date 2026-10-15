package duncan;

/**
 * Signals a Duncan-specific error condition arising from invalid user
 * input (e.g. an unrecognized command, a malformed task description).
 */
public class DuncanException extends Exception {
    /**
     * Creates an exception carrying a message to show the user.
     *
     * @param message The user-facing explanation of what went wrong.
     */
    public DuncanException(String message) {
        super(message);
    }
}
