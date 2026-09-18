package blud.exception;

/** Signals that a task duplicates an existing task's details. */
public class DuplicateTaskException extends RuntimeException {
    /** Creates the default duplicate-task exception. */
    public DuplicateTaskException() {
        super("A task with the same details already exists");
    }
}
