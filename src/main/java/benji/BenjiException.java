package benji;

/**
 * Represents an error caused by an invalid BENJI command.
 */
public class BenjiException extends Exception {

    /**
     * Creates an exception containing a user-facing error message.
     *
     * @param message explanation of the invalid command
     */
    public BenjiException(String message) {
        super(message);
    }
}
