package rem.exception;

/**
 * Indicates that a task creation command does not contain a task description.
 */
public class EmptyDescriptionException extends RemException {
    private static final String MESSAGE = "You didn't say what you wanna do...";

    /**
     * Creates an exception for a missing task description.
     */
    public EmptyDescriptionException() {
        super(MESSAGE);
    }
}
