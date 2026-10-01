package victoria.exception;

/** Indicates that no more tasks can be added. */
public class TaskListFullException extends TaskListException {
    /** Creates a full-list error. */
    public TaskListFullException(String message) { super(message); }
}
