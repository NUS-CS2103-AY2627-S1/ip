package ev;

/**
 * Represents an error caused by input E.V. cannot accept.
 * Checked (extends {@code Exception}) so callers must handle invalid user input explicitly.
 */
public class EvException extends Exception {
    /**
     * Constructs an exception carrying the given user-facing error message.
     *
     * @param message the message shown to the user.
     */
    public EvException(String message) {
        super(message);
    }
}
