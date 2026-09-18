package oreo.core;


/**
 * Represents an expected input error reported by the Oreo chatbot.
 */
public class OreoException extends Exception {
    /**
     * Creates an input error with the message shown to the user.
     *
     * @param message the explanation of the invalid input
     */
    public OreoException(String message) {
        super(message);
    }
}
