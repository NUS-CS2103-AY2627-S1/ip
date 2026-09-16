package aider;

/**
 * Represents an error caused by invalid input to the Aider chatbot.
 */
public class AiderException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation of the input error
     */
    public AiderException(String message) {
        super(message);
    }
}
