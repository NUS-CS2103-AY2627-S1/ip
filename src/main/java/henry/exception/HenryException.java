package henry.exception;

/**
 * Represents an error caused by an invalid command given to Henry.
 */
public class HenryException extends Exception {
    /**
     * Creates an exception with an explanation for the user.
     *
     * @param message explanation of the invalid command.
     */
    public HenryException(String message) {
        super(message);
    }
}
