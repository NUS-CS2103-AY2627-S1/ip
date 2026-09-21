package janet.exception;

/** Indicates that Janet has reached its task capacity. */
public class TaskLimitException extends JanetException {
    /**
     * Creates an exception with a user-facing validation message.
     *
     * @param message message to display to the user.
     */
    public TaskLimitException(String message) {
        super(message);
    }
}
