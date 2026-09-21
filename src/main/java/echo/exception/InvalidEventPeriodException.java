package echo.exception;

/**
 * Signals that an event does not end after it starts.
 */
public class InvalidEventPeriodException extends EchoException {
    /** Creates the error shown for an invalid event period. */
    public InvalidEventPeriodException() {
        super("An event must start before it ends.");
    }
}
