package kelore.exception;

/** Represents an event whose end is not later than its start. */
public class InvalidEventPeriodException extends KeloreInputException {
    /** Creates an exception explaining the required event time ordering. */
    public InvalidEventPeriodException() {
        super("The event end date/time must be later than its start date/time.");
    }
}
