package rem.exception;

/**
 * Indicates that a command does not contain a valid date.
 */
public class InvalidDateException extends RemException {
    private static final String MESSAGE = "Please use a date like 2019-10-15.";

    /**
     * Creates an exception for a missing or invalid date.
     */
    public InvalidDateException() {
        super(MESSAGE);
    }
}
