package atom.exception;

/**
 * An exception for a date that does not exist in the calendar.
 */
public class AtomImpossibleDateException extends AtomException {
    /**
     * Initializes an <code>AtomImpossibleDateException</code>.
     */
    public AtomImpossibleDateException() {
        super("Oh no! That date does not exist. Please enter a valid calendar date.");
    }
}
