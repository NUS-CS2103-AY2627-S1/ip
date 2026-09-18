package blud.exception;

/** Signals that an event task is invalid or incomplete. */
public class EventException extends RuntimeException {
    /** Creates an exception for an event task without a description. */
    public EventException() {
        super("Missing description of event task");
    }
    /** Creates an exception with a caller-supplied explanation. */
    public EventException(String message) {
        super(message);
    }
}
