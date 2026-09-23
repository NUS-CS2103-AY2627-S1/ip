package max.maxexception;

/** Indicates that a task date is earlier than the current date. */
public class DateBeforeCurrentDateException extends MaxException {
    public DateBeforeCurrentDateException() {
        super("Invalid date. The date cannot be before the current date.");
    }
}
