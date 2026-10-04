package penny.common;

/**
 * Custom exception class for handling domain-specific errors in Penny.
 */
public class PennyException extends Exception {

    /**
     * Constructs a PennyException with the specified error message.
     *
     * @param message The detailed error message.
     */
    public PennyException(String message) {
        super(message);
    }
}
