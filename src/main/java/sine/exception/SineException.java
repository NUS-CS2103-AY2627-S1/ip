package sine.exception;

/**
 * Represents an input error specific to the Sine chatbot.
 */
public class SineException extends Exception {
    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message Explanation of the invalid input.
     */
    public SineException(String message) {
        super(message);
    }
}
