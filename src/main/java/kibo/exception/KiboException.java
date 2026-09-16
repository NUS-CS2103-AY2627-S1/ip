package kibo.exception;

/**
 * Represents an error specific to the Kibo chatbot.
 */
public class KiboException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a chatbot-specific exception with a user-facing message.
     *
     * @param message explanation of the invalid command.
     */
    public KiboException(String message) {
        super(message);
    }
}
