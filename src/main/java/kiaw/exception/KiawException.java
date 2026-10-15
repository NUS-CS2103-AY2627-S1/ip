package kiaw.exception;

/**
 * Represents an error caused by an invalid command or operation in Kiaw.
 */
public class KiawException extends Exception {

    /**
     * Creates a KiawException with the specified error message.
     *
     * @param message description of the error
     */
    public KiawException(String message) {
        super(message);
    }
}
