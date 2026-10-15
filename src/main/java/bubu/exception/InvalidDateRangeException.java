package bubu.exception;

/** Indicates that an event does not have a valid chronological range. */
public class InvalidDateRangeException extends BubuException {
    private static final String INVALID_DATE_RANGE_MESSAGE =
            "Meow! An event's end date and time must be after its start date and time.";

    /** Creates an error for an event whose end is not after its start. */
    public InvalidDateRangeException() {
        super(INVALID_DATE_RANGE_MESSAGE);
    }
}
