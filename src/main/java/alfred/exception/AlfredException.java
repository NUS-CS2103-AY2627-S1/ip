package alfred.exception;

/**
 * Represents an error caused by an invalid command entered into Alfred.
 */
public class AlfredException extends Exception {
    /**
     * Creates an Alfred-specific error with a message for the user.
     *
     * @param message explanation of how the command should be corrected
     */
    public AlfredException(String message) {
        super(message);
    }
}
