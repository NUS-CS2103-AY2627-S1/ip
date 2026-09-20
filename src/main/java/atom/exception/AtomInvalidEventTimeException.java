package atom.exception;

/**
 * An exception for an event whose end time is not after its start time.
 */
public class AtomInvalidEventTimeException extends AtomException {
    /**
     * Initializes an <code>AtomInvalidEventTimeException</code>.
     */
    public AtomInvalidEventTimeException() {
        super("Oh no! The end time must be after the start time.");
    }
}
