package ultron.exception;

/**
 * Represents an input error that should be shown to the chatbot user.
 */
public class UltronException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message the explanation of the input error
     */
    public UltronException(String message) {
        super(message);
    }
}
