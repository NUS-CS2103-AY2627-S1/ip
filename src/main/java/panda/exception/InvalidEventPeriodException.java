package panda.exception;

/**
 * Signals that an event starts after its stated ending date and time.
 */
public class InvalidEventPeriodException extends InvalidDateException {
    private static final String ERROR_MESSAGE =
            "OOPS!!! This panda needs an event to start no later than it ends.";

    /**
     * Creates an error explaining the required ordering of event endpoints.
     */
    public InvalidEventPeriodException() {
        super(ERROR_MESSAGE);
    }
}
