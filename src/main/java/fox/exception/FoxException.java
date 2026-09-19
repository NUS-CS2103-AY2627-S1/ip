package fox.exception;

/**
 * Represents an error caused by an invalid Fox command.
 */
public class FoxException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message the explanation of the command error
     */
    public FoxException(String message) {
        super(message);
    }
}
