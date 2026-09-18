package rem.exception;

/**
 * Indicates that an event command does not contain a valid start and end time.
 */
public class InvalidEventFormatException extends RemException {
    private static final String MESSAGE =
            "I need to know when it starts and when it ends..."
            + "\nUse: event DESCRIPTION /from YYYY-MM-DD [HHmm] /to YYYY-MM-DD [HHmm]"
            + "\nExample: event meeting /from 2026-10-01 1400 /to 2026-10-01 1500"
            + "\nThe end must be after the start.";

    /**
     * Creates an exception for missing, empty, or malformed event time information.
     */
    public InvalidEventFormatException() {
        super(MESSAGE);
    }
}
