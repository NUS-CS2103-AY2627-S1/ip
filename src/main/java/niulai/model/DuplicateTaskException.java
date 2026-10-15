package niulai.model;

/**
 * Signals that a task list already contains a task with the same normalized identity.
 */
public class DuplicateTaskException extends IllegalArgumentException {
    /** Creates a duplicate-task error. */
    public DuplicateTaskException() {
        super("That task already exists.");
    }
}
