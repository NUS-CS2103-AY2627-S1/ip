package rem.exception;

/**
 * Represents an expected error caused by invalid user input to Rem.
 */
public class RemException extends Exception {
    /**
     * Creates a Rem exception with an explanation suitable for displaying to the user.
     *
     * @param message Explanation of the input error.
     */
    public RemException(String message) {
        super(message);
    }
}
