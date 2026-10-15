package charliek.exception;

/**
 * Indicates that a deadline or event date/time is not in a supported format.
 */
public class InvalidDateTimeException extends CharlieKException {
    /**
     * The message displayed when a date/time cannot be parsed.
     */
    private static final String MESSAGE =
            "I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm. Keep it winning.";

    /**
     * Creates an exception with the user-facing date/time guidance.
     */
    public InvalidDateTimeException() {
        super(MESSAGE);
    }
}
