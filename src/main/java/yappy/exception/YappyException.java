package yappy.exception;

/**
 * Represents an error caused by invalid user input in the Yappy chatbot.
 */
public class YappyException extends Exception {
    /** Version identifier used when an exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a Yappy-specific exception with a user-friendly message.
     */
    public YappyException(String message) {
        super(message);
    }
}
