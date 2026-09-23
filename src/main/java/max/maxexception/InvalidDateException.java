package max.maxexception;

/** Indicates that a date or date-time does not use the supported format. */
public class InvalidDateException extends MaxException {
    public InvalidDateException() {
        super("Invalid date. Use YYYY-MM-DD or YYYY-MM-DD HH:MM.");
    }
}
