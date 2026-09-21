package janet.exception;

/** Indicates that a structured command is malformed. */
public class InvalidCommandException extends JanetException {
    /**
     * Creates an exception with a user-facing validation message.
     *
     * @param message message to display to the user.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
