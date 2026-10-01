package victoria.exception;

/** Base class for errors caused by task-list operations. */
public class TaskListException extends VictoriaException {
    /** Creates a task-list error. */
    public TaskListException(String message) { super(message); }
}
