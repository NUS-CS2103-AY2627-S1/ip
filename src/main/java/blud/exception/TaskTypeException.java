package blud.exception;

/** Signals that a task type is not supported. */
public class TaskTypeException extends RuntimeException {
    /** Creates an exception with the default task-type message. */
    public TaskTypeException() {
        super("Please specify one of the message types todo, event or deadline");
    }
    /** Creates an exception with a caller-supplied explanation. */
    public TaskTypeException(String message) {
        super(message);
    }
}
