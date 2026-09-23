package max.maxexception;

/** Indicates that an event's end date precedes its start date. */
public class InvalidDateRangeException extends MaxException {
    public InvalidDateRangeException() {
        super("Invalid date range. The end date cannot be before the start date.");
    }
}
