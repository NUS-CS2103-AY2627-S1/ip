package baymax.exception;

/**
 * Represents an event command with no start-time description.
 */
public class EmptyFromException extends BaymaxException {

    /**
     * Creates an exception for a missing event start time.
     */
    public EmptyFromException() {
        super(" Sorry, an event needs a start time.");
    }
}
