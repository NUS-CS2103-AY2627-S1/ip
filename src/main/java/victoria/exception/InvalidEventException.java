package victoria.exception;

/** Indicates that an event command has invalid timing information. */
public class InvalidEventException extends InvalidTaskException {
    /** Creates an invalid-event error. */
    public InvalidEventException(String message) { super(message); }
}
