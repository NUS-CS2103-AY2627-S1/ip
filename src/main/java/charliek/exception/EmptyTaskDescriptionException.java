package charliek.exception;

/**
 * Indicates that a task command does not include a description.
 */
public class EmptyTaskDescriptionException extends CharlieKException {
    /**
     * The message displayed when a task description is missing.
     */
    private static final String MESSAGE =
            "The description is empty! Bring the energy and enter it, or I will carry the flame!";

    /**
     * Creates an exception for a missing task description.
     */
    public EmptyTaskDescriptionException() {
        super(MESSAGE);
    }
}
