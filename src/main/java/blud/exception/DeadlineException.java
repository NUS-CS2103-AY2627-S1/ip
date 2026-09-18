package blud.exception;

/** Signals that a deadline task is invalid or incomplete. */
public class DeadlineException extends RuntimeException {
    /** Creates an exception for a deadline task without a description. */
    public DeadlineException() {
        super("Missing description of deadline task");
    }
    /** Creates an exception with a caller-supplied explanation. */
    public DeadlineException(String message) {
        super(message);
    }
}
