package chausistant.exception;

/**
 * Represents a user-facing command error that does not end the chatbot.
 */
public class ChausistantException extends Exception {

    /** Creates an error that can be shown directly to the user. */
    public ChausistantException(String message) {
        super(message);
    }
}
