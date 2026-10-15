package bubu.exception;

/**
 * Indicates that a date or date-time value does not use an accepted format.
 */
public class InvalidDateTimeException extends BubuException {
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Meow! Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2026-08-31 1800).";

    /**
     * Creates the standard date-time format error.
     */
    public InvalidDateTimeException() {
        super(INVALID_DATE_TIME_MESSAGE);
    }

    /**
     * Creates an error with a custom date-time message.
     *
     * @param customMessage user-facing date-time error message.
     */
    public InvalidDateTimeException(String customMessage) {
        super(customMessage);
    }
}
