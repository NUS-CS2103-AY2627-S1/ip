package victoria.exception;

/** Indicates that a task number does not identify a task. */
public class InvalidTaskNumberException extends TaskListException {
    /** Creates an invalid-task-number error. */
    public InvalidTaskNumberException(String message) { super(message); }
}
