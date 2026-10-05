package kaykay.exception;

/**
 * Represents an expected input error reported by the Kaykay chatbot.
 */
public class KaykayException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with a message that can be shown to the user.
     *
     * @param message explanation of the error and how to correct it.
     */
    public KaykayException(String message) {
        super(message);
    }
}
