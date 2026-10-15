package charliek.exception;

/**
 * Indicates that a task with the same details already exists.
 */
public class DuplicateTaskException extends CharlieKException {
    /**
     * Creates an exception for a duplicate task.
     */
    public DuplicateTaskException() {
        super("That task is already on the list. Bring a new winner to the board.");
    }
}
