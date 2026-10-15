package nutrisoy.exception;

/**
 * Represents an error caused by an invalid user command or task data.
 */
public class DukeException extends Exception {
    /**
     * Creates an exception with the supplied explanation.
     *
     * @param message explanation of the error
     */
    public DukeException(String message) {
        super(message);
    }
}
