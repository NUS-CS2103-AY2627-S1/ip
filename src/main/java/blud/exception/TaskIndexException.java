package blud.exception;

/** Signals that a command refers to a task number outside the current list. */
public class TaskIndexException extends RuntimeException {
    /** Creates a task-index exception with a user-facing explanation. */
    public TaskIndexException(String message) {
        super(message);
    }
}
