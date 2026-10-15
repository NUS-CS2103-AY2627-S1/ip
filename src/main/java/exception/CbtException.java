package exception;

/**
 * Represents an error caused by an invalid command entered into CBT.
 */
public class CbtException extends Exception {

    /**
     * Creates an exception with a message explaining how the user can correct the command.
     *
     * @param message explanation shown to the user.
     */
    public CbtException(String message) {
        super(message);
    }
}
