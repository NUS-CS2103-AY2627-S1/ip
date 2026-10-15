package charliek.exception;

/**
 * Indicates that an event does not finish after it starts.
 */
public class InvalidEventRangeException extends CharlieKException {
    /**
     * Creates an exception for an invalid event range.
     */
    public InvalidEventRangeException() {
        super("An event must end after it starts. Keep the timeline winning.");
    }
}
