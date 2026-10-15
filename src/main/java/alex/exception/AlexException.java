package alex.exception;

/**
 * Signals that a user command cannot be completed.
 */
public class AlexException extends Exception {
    /**
     * Creates an exception with an explanatory message.
     *
     * @param message explanation of the command error.
     */
    public AlexException(String message) {
        super(message);
    }
}
