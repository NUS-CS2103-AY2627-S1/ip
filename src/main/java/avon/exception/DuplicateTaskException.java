package avon.exception;

/**
 * Signals that a task being added has the same details as an existing task.
 */
public class DuplicateTaskException extends AvonException {
    /**
     * Creates an error explaining that the task already exists.
     */
    public DuplicateTaskException() {
        super("That task already dwells within thy list.");
    }
}
