package victoria.exception;

/** Base class for errors in a task's description or timing information. */
public class InvalidTaskException extends VictoriaException {
    /** Creates an invalid-task error. */
    public InvalidTaskException(String message) { super(message); }
}
