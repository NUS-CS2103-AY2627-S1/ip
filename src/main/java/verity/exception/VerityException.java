package verity.exception;

/**
 * Represents an error caused by invalid user input or corrupted saved data.
 */
public class VerityException extends Exception {

    /**
     * Creates a Verity exception with the specified message.
     *
     * @param message Explanation of the error.
     */
    public VerityException(String message) {
        super(message);
    }
}
