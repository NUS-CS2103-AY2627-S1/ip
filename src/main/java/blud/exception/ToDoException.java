package blud.exception;

/** Signals that a todo task is invalid or incomplete. */
public class ToDoException extends RuntimeException {
    /** Creates an exception for a todo task without a description. */
    public ToDoException() {
        super("Missing description of todo task");
    }
    /** Creates an exception with a caller-supplied explanation. */
    public ToDoException(String message) {
        super(message);
    }
}
