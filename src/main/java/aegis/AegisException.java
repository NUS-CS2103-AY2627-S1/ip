package aegis;

/**
 * Represents an error that occurs within the Aegis chatbot.
 */
public class AegisException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message Explanation of the input error.
     */
    public AegisException(String message) {
        super(message);
    }
}
