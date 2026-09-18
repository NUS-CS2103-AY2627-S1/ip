package rem.exception;

/**
 * Indicates that a deadline command does not contain valid due information.
 */
public class InvalidDeadlineFormatException extends RemException {
    private static final String MESSAGE = "When is this due by again?"
            + "\nUse: deadline DESCRIPTION /by YYYY-MM-DD [HHmm]"
            + "\nExample: deadline return book /by 2026-10-01 1800";

    /**
     * Creates an exception for a missing or empty deadline due value.
     */
    public InvalidDeadlineFormatException() {
        super(MESSAGE);
    }
}
