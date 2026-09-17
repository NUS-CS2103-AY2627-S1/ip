package panda.exception.task;

import panda.exception.ApplicationException;

/**
 * Signals that an event's start and end parameters do not form a valid time range.
 */
public class InvalidEventParametersException extends ApplicationException {
    private static final String MESSAGE_INVALID_RANGE =
            "Congratulations, you scheduled time backwards. The event must end after it starts.";

    /**
     * Creates an exception explaining that an event must end after it starts.
     */
    public InvalidEventParametersException() {
        super(MESSAGE_INVALID_RANGE);
    }
}
