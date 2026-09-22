package panda.exception;

/**
 * Signals that a supplied date and time is invalid or incorrectly formatted.
 */
public class InvalidDateException extends PandaException {
    private static final String DATE_TIME_ERROR_MESSAGE =
            "OOPS!!! This panda needs a valid date and time in "
                    + "yyyy-MM-dd HH:mm format.";
    private static final String LIST_DATE_ERROR_MESSAGE =
            "OOPS!!! This panda needs a valid list date in yyyy-MM-dd format.";

    /**
     * Creates a user-facing error that explains Panda's accepted format.
     */
    public InvalidDateException() {
        super(DATE_TIME_ERROR_MESSAGE);
    }

    /**
     * Creates an error with a message for a particular date-input context.
     *
     * @param message the user-facing validation message.
     */
    protected InvalidDateException(String message) {
        super(message);
    }

    /**
     * Creates an error for an invalid date supplied to {@code list}.
     *
     * @return an error explaining the list-date format.
     */
    public static InvalidDateException createForListDate() {
        return new InvalidDateException(LIST_DATE_ERROR_MESSAGE);
    }
}
