package janet.exception;

/** Indicates that a task number is invalid or does not exist. */
public class InvalidTaskException extends JanetException {
    /**
     * Creates an exception with a user-facing validation message.
     *
     * @param message message to display to the user.
     */
    public InvalidTaskException(String message) {
        super(message);
    }
}
