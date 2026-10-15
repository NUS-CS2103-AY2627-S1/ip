package benbot;

/** Indicates that a task number is missing, malformed, or outside the task list. */
public class InvalidTaskNumberException extends BenBotException {
    /** The version identifier used when this exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly task-number error message.
     *
     * @param message the explanation of the invalid task number.
     */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
