package tblade.exception;

/**
 * Represents an error caused by an invalid command entered into TBlade.
 */
public class TBladeException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid command
     */
    public TBladeException(String message) {
        super(message);
    }
}
