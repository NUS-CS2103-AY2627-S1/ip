package tasque;

/**
 * Represents an expected error while loading tasks or processing a Tasque command.
 */
public class TasqueException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param errorMessage Message describing the error.
     */
    public TasqueException(String errorMessage) {
        super(errorMessage);
    }
}
