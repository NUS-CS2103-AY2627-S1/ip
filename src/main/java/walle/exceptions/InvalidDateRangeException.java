package walle.exceptions;

/**
 * Thrown when an Event's start date/time is not strictly before its end
 * date/time.
 */
public class InvalidDateRangeException extends WALLEException {

    /**
     * Creates an InvalidDateRangeException.
     */
    public InvalidDateRangeException() {
        super("\n   ERROR :( An event's start must be before its end!\n");
    }

}
