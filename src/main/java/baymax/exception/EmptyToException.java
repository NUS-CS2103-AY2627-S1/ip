package baymax.exception;

/**
 * Represents an event command with no end-time description.
 */
public class EmptyToException extends BaymaxException {

    /**
     * Creates an exception for a missing event end time.
     */
    public EmptyToException() {
        super(" Sorry, an event needs an end time.");
    }
}
