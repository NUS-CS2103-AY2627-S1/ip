package leo;

/**
 * Signals that Leo could not understand or carry out a user command,
 * e.g. because of a missing argument or an invalid format. The
 * message is written to be shown to the user as-is.
 */
public class LeoException extends Exception {
    /**
     * Creates a new LeoException with a user-facing message.
     *
     * @param message message to show the user
     */
    public LeoException(String message) {
        super(message);
    }
}
