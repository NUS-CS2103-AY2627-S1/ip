package es;

/**
 * Represents an error caused by invalid input to the Es chatbot.
 */
public class EsException extends Exception {
    /**
     * Creates an Es-specific exception with a message for the user.
     *
     * @param message the explanation of the invalid input
     */
    public EsException(String message) {
        super(message);
    }
}
