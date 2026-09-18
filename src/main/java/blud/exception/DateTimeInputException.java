package blud.exception;

/** Signals that a date or time is missing, malformed, or not a real value. */
public class DateTimeInputException extends RuntimeException {
    /** Creates a date-time exception with a user-facing explanation. */
    public DateTimeInputException(String message) {
        super(message);
    }
}
