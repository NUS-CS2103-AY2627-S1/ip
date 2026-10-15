package labubu;

/** Signals a date or time value that does not follow the supported format. */
public class InvalidDateTimeException extends Exception {
    /** Creates an exception with the message shown for invalid date or time values. */
    public InvalidDateTimeException() {
        super("Oops! Invalid date/time. Use dd/MM/yyyy or dd/MM/yyyy HH:mm.");
    }
}
