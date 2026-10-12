package bogos;

/**
 * Signals an error caused by an invalid Bogos command or task operation.
 */
public class BogosException extends Exception {
    /**
     * Creates an exception with the message shown to the user.
     *
     * @param message Description of the error.
     */
    public BogosException(String message) {
        super(message);
    }
}
