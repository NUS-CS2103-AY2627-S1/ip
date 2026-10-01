package victoria.exception;

/** Indicates that a deadline command has invalid timing information. */
public class InvalidDeadlineException extends InvalidTaskException {
    /** Creates an invalid-deadline error. */
    public InvalidDeadlineException(String message) { super(message); }
}
