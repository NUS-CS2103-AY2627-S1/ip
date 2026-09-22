package panda.exception;

/**
 * Signals that a deadline or event is missing required timing information.
 *
 * Reports missing separators or values without attempting to
 * validate the user's date and time format.
 */
public class MissingDateTimeException extends PandaException {
    /**
     * Creates an error that shows the expected command structure.
     *
     * @param usage the command structure the user should follow.
     */
    public MissingDateTimeException(String usage) {
        super("OOPS!!! This panda needs more timing details. Try: " + usage);
    }
}
