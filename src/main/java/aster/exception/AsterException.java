package aster.exception;

/**
 * Represents an invalid command entered into the Aster chatbot.
 */
public class AsterException extends Exception {
    private final String suggestion;

    /**
     * Creates an exception with an explanation and a way to correct the command.
     */
    public AsterException(String message, String suggestion) {
        super(message);
        this.suggestion = suggestion;
    }

    /**
     * Returns the complete user-facing error response.
     */
    public String getUserMessage() {
        return "Error: " + getMessage() + System.lineSeparator() + "Try: " + suggestion;
    }
}
