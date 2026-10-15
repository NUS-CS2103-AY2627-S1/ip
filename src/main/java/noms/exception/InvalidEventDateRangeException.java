package noms.exception;

/** Indicates that an event does not end after it starts. */
public class InvalidEventDateRangeException extends NomsException {
    /** Creates an exception explaining the required event date order. */
    public InvalidEventDateRangeException() {
        super("This event's end date must be after its start date.\n"
                + "Try an end date later than the start date.");
    }
}
