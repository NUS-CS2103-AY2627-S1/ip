package nova.exception;

/**
 * Represents an error caused by an invalid command given to Nova.
 */
public class NovaException extends Exception {
    /**
     * Creates a chatbot-specific exception with a user-friendly explanation.
     *
     * @param message explanation of the invalid input and how to correct it.
     */
    public NovaException(String message) {
        super(message);
    }
}
