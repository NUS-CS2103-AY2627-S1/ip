package sophon.exception;

/**
 * Represents an error caused by input that Sophon cannot understand.
 */
public class SophonException extends Exception {
    /**
     * Creates a Sophon-specific exception with the message to show the user.
     *
     * @param message explanation of the input error
     */
    public SophonException(String message) {
        super(message);
    }
}
