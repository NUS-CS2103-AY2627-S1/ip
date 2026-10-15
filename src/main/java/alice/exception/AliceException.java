package alice.exception;

/**
 * Represents an error caused by invalid Alice input or task storage data.
 */
public class AliceException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message The explanation of the error.
     */
    public AliceException(String message) {
        super(message);
    }
}
