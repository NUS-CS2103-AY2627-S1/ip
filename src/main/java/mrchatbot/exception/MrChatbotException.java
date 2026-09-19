package mrchatbot.exception;

/**
 * Represents an error caused by invalid user input to Mr Chatbot.
 */
public class MrChatbotException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     */
    public MrChatbotException(String message) {
        super(message);
    }
}
